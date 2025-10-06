package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.PaymentRequest;
import likeUniquloWeb.dto.response.PaymentResponse;
import likeUniquloWeb.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = "*")
public class PaymentController {
    PaymentService paymentService;

    @PostMapping
    public PaymentResponse create(@RequestBody PaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @GetMapping("/order/{orderId}")
    public List<PaymentResponse> getByOrder(@PathVariable Long orderId) {
        return paymentService.getPaymentsByOrder(orderId);
    }

    @PutMapping("/{id}/status")
    public PaymentResponse updateStatus(@PathVariable Long id, @RequestParam String status) {
        return paymentService.updateStatus(id, status);
    }

}
