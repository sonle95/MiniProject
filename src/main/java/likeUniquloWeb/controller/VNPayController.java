package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.VNPayRequest;
import likeUniquloWeb.service.VNPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class VNPayController {

    private final VNPayService vnPayService;

    @PostMapping("/create")
    public String createPayment(
            @RequestBody VNPayRequest request,
            HttpServletRequest httpRequest) {

        log.info("Creating VNPay payment for order: {}", request.getOrderId());
    try {
        String paymentUrl = vnPayService.createPaymentUrl(request, httpRequest);
        return paymentUrl;
    }catch (Exception e){
      log.error("Error", e);
      return "Error";
    }
    }

    /**
     * Callback từ VNPay sau khi thanh toán
     * GET /api/payment/callback
     */
    @GetMapping("/callback")
    public Map<String, String> paymentCallback(
            @RequestParam Map<String, String> params) {

        log.info("VNPay callback received with params: {}", params);
        try{
        Map<String, String> result = vnPayService.handleCallback(params);
        return result;
        }catch (Exception e){
            log.error("Error", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return errorResponse;
        }
    }

    @GetMapping("/query/{orderId}")
    public Map<String, String> queryPayment(
            @PathVariable Long orderId) {

        log.info("Querying payment status for order: {}", orderId);
        try {
            Map<String, String> result = vnPayService.queryPaymentStatus(orderId);

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
