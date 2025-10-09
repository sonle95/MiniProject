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

    /**
     * Tạo URL thanh toán VNPay
     */
    public String createPaymentUrl(VNPayRequest request, HttpServletRequest httpRequest) {
        // Lấy thông tin order
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Tạo các tham số cho VNPay
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnPayConfig.getVersion());
        vnpParams.put("vnp_Command", vnPayConfig.getCommand());
        vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());

        // Số tiền (VNPay yêu cầu nhân 100, đơn vị VNĐ)
        long amount = order.getTotalAmount().longValue() * 100;
        vnpParams.put("vnp_Amount", String.valueOf(amount));

        vnpParams.put("vnp_CurrCode", "VND");

        // Mã giao dịch - unique
        String txnRef = order.getOrderNumber() + "_" + System.currentTimeMillis();
        vnpParams.put("vnp_TxnRef", txnRef);

        vnpParams.put("vnp_OrderInfo", "Thanh toan don hang: " + order.getOrderNumber());
        vnpParams.put("vnp_OrderType", vnPayConfig.getOrderType());
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
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
                // Build hash data (KHÔNG URL encode)
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(fieldValue);  // ✅ KHÔNG encode

                // Build query (CÓ URL encode)
                try {
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                } catch (UnsupportedEncodingException e) {
                    log.error("Encoding error", e);
                }

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
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
        log.info("Hash Data: {}", hashData.toString());
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

        // Lấy secure hash từ params
        String vnpSecureHash = params.get("vnp_SecureHash");

        // Remove vnp_SecureHash để verify
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        // Verify signature
        String signValue = VNPayUtil.hashAllFields(params, vnPayConfig.getHashSecret());

        if (signValue.equals(vnpSecureHash)) {
            String responseCode = params.get("vnp_ResponseCode");
            String txnRef = params.get("vnp_TxnRef");
            String amount = params.get("vnp_Amount");
            String orderInfo = params.get("vnp_OrderInfo");
            String transactionNo = params.get("vnp_TransactionNo");
            String payDate = params.get("vnp_PayDate");

            // Extract order number từ txnRef
            String orderNumber = txnRef.split("_")[0];

            Order order = orderRepository.findByOrderNumber(orderNumber)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            if ("00".equals(responseCode)) {
                // Thanh toán thành công
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

                log.info("Payment successful for order: {}", orderNumber);
            } else {
                // Thanh toán thất bại
                order.setPaymentStatus(PaymentStatus.FAILED);
                orderRepository.save(order);

                result.put("status", "failed");
                result.put("message", "Payment failed with code: " + responseCode);
                result.put("orderId", order.getId().toString());
                result.put("orderNumber", orderNumber);
                result.put("responseCode", responseCode);

                log.warn("Payment failed for order: {} with code: {}", orderNumber, responseCode);
            }
        } else {
            // Invalid signature
            result.put("status", "error");
            result.put("message", "Invalid signature");
            log.error("Invalid VNPay signature");
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
        result.put("paymentMethod", order.getPaymentMethod());
        result.put("totalAmount", order.getTotalAmount().toString());

        return result;
    }
}
