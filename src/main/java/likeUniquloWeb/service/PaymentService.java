package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.PaymentRequest;
import likeUniquloWeb.dto.response.PaymentResponse;
import likeUniquloWeb.entity.Order;
import likeUniquloWeb.entity.Payment;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.PaymentMapper;
import likeUniquloWeb.repository.OrderRepository;
import likeUniquloWeb.repository.PaymentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    PaymentRepository paymentRepository;
    PaymentMapper paymentMapper;
    OrderRepository orderRepository;

    public PaymentResponse createPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        Payment payment = paymentMapper.toEntity(request);
        payment.setOrder(order);
        payment.setStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);

        return paymentMapper.toDto(saved);
    }

    public List<PaymentResponse> getPaymentsByOrder(Long orderId) {
        return paymentRepository.findByOrder_Id(orderId)
                .stream().map(paymentMapper::toDto).toList();
    }


    public PaymentResponse updateStatus(Long paymentId, String status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        payment.setStatus(status);
        return paymentMapper.toDto(paymentRepository.save(payment));
    }


}
