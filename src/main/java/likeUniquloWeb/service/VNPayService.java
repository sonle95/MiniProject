package likeUniquloWeb.service;

import likeUniquloWeb.configuration.VNPayConfig;
import likeUniquloWeb.dto.request.VNPayRequest;
import likeUniquloWeb.entity.Order;
import likeUniquloWeb.enums.PaymentStatus;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.VNPayUtil;
import likeUniquloWeb.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VNPayService {

    private final VNPayConfig vnPayConfig;
    private final OrderRepository orderRepository;


    public String createPaymentUrl(VNPayRequest request, HttpServletRequest httpRequest) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));


        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnPayConfig.getVersion());
        vnpParams.put("vnp_Command", vnPayConfig.getCommand());
        vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());

        // Số tiền (VNPay yêu cầu nhân 100, đơn vị VNĐ)
        // ✅ FIX: Kiểm tra số tiền hợp lệ (tối thiểu 10,000 VNĐ = 1,000,000 trong params)
        long amount = order.getTotalAmount().longValue() * 100;
        if (amount < 1000000) {
            log.warn("Amount {} is too small, setting to minimum 1,000,000", amount);
            amount = 1000000; // 10,000 VNĐ
        }
        vnpParams.put("vnp_Amount", String.valueOf(amount));

        vnpParams.put("vnp_CurrCode", "VND");

        // Mã giao dịch - unique
        String txnRef = order.getOrderNumber() + "_" + System.currentTimeMillis();
        vnpParams.put("vnp_TxnRef", txnRef);

        vnpParams.put("vnp_OrderInfo", "Thanh toan don hang: " + order.getOrderNumber());
        vnpParams.put("vnp_OrderType", vnPayConfig.getOrderType());
        vnpParams.put("vnp_Locale", "vn");

        // ✅ FIX: Thêm bankCode vào return URL nếu có
        String returnUrl = vnPayConfig.getReturnUrl();
        if (request.getBankCode() != null && !request.getBankCode().isEmpty()) {
            returnUrl += (returnUrl.contains("?") ? "&" : "?") + "bankCode=" + request.getBankCode();
        }
        vnpParams.put("vnp_ReturnUrl", returnUrl);

        vnpParams.put("vnp_IpAddr", VNPayUtil.getIpAddress(httpRequest));

        // Thời gian tạo và hết hạn
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_CreateDate", vnpCreateDate);

        cld.add(Calendar.MINUTE, 15); // Hết hạn sau 15 phút
        String vnpExpireDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_ExpireDate", vnpExpireDate);

        // Bank code (optional)
        if (request.getBankCode() != null && !request.getBankCode().isEmpty()) {
            vnpParams.put("vnp_BankCode", request.getBankCode());
        }

        // Sắp xếp params và tạo query string
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnpParams.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                try {
                    // ✅ URL encode cho cả hashData và query
                    String encodedName = URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString());
                    String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString());

                    // Build hash data (✅ ĐÃ URL encode)
                    hashData.append(encodedName);
                    hashData.append('=');
                    hashData.append(encodedValue);

                    // Build query (✅ ĐÃ URL encode)
                    query.append(encodedName);
                    query.append('=');
                    query.append(encodedValue);

                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                } catch (UnsupportedEncodingException e) {
                    log.error("Encoding error", e);
                }
            }
        }

        String queryUrl = query.toString();
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnpUrl() + "?" + queryUrl;

        log.info("Generated VNPay payment URL for order: {}", order.getOrderNumber());
        log.info("=== VNPay Parameters ===");
        vnpParams.forEach((key, value) -> log.info("{} = {}", key, value));
        log.info("Hash Data (URL Encoded): {}", hashData.toString());
        log.info("Hash Secret: {}", vnPayConfig.getHashSecret());
        log.info("vnp_SecureHash: {}", vnpSecureHash);
        log.info("Payment URL: {}", paymentUrl);
        log.info("=========================");
        return paymentUrl;
    }

    /**
     * Xử lý callback từ VNPay
     */
    @Transactional
    public Map<String, String> handleCallback(Map<String, String> params) {
        Map<String, String> result = new HashMap<>();

        try {
            // Lấy secure hash từ params
            String vnpSecureHash = params.get("vnp_SecureHash");

            if (vnpSecureHash == null || vnpSecureHash.isEmpty()) {
                result.put("status", "error");
                result.put("message", "Missing vnp_SecureHash");
                log.error("Missing vnp_SecureHash in callback");
                return result;
            }

            // Clone params để không bị thay đổi
            Map<String, String> paramsClone = new HashMap<>(params);

            // Remove vnp_SecureHash để verify
            paramsClone.remove("vnp_SecureHash");
            paramsClone.remove("vnp_SecureHashType");

            // Verify signature
            String signValue = VNPayUtil.hashAllFields(paramsClone, vnPayConfig.getHashSecret());

            log.info("VNPay Callback - Expected: {}, Actual: {}", signValue, vnpSecureHash);

            if (signValue.equals(vnpSecureHash)) {
                String responseCode = params.get("vnp_ResponseCode");
                String txnRef = params.get("vnp_TxnRef");
                String amount = params.get("vnp_Amount");
                String orderInfo = params.get("vnp_OrderInfo");
                String transactionNo = params.get("vnp_TransactionNo");
                String payDate = params.get("vnp_PayDate");

                // Extract order number từ txnRef
                String orderNumber = txnRef != null ? txnRef.split("_")[0] : "";

                Order order = orderRepository.findByOrderNumber(orderNumber)
                        .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

                if ("00".equals(responseCode)) {
                    // ✅ Thanh toán thành công
                    order.setPaymentStatus(PaymentStatus.PAID);
                    order.setPaymentMethod("VNPAY");
                    orderRepository.save(order);

                    result.put("status", "success");
                    result.put("message", "Payment successful");
                    result.put("orderId", order.getId().toString());
                    result.put("orderNumber", orderNumber);
                    result.put("transactionNo", transactionNo);
                    result.put("amount", amount);
                    result.put("payDate", payDate);

                    log.info("✅ Payment successful for order: {} | TxnNo: {}", orderNumber, transactionNo);
                } else {
                    // ❌ Thanh toán thất bại
                    order.setPaymentStatus(PaymentStatus.FAILED);
                    orderRepository.save(order);

                    result.put("status", "failed");
                    result.put("message", "Payment failed with code: " + responseCode);
                    result.put("orderId", order.getId().toString());
                    result.put("orderNumber", orderNumber);
                    result.put("responseCode", responseCode);

                    log.warn("❌ Payment failed for order: {} with code: {}", orderNumber, responseCode);
                }
            } else {
                // ❌ Invalid signature
                result.put("status", "error");
                result.put("message", "Invalid signature");
                log.error("❌ Invalid VNPay signature. Expected: {}, Got: {}", signValue, vnpSecureHash);
            }
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
            log.error("❌ Error handling VNPay callback", e);
        }

        return result;
    }

    /**
     * Query payment status
     */
    public Map<String, String> queryPaymentStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        Map<String, String> result = new HashMap<>();
        result.put("orderId", order.getId().toString());
        result.put("orderNumber", order.getOrderNumber());
        result.put("paymentStatus", order.getPaymentStatus().toString());
        result.put("paymentMethod", order.getPaymentMethod() != null ? order.getPaymentMethod() : "NONE");
        result.put("totalAmount", order.getTotalAmount().toString());

        return result;
    }
}