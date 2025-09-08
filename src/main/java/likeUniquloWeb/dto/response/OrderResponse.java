package likeUniquloWeb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import likeUniquloWeb.enums.OrderStatus;
import likeUniquloWeb.enums.PaymentStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {

    Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String orderNumber;
    BigDecimal totalAmount;

    List<OrderItemResponse> orderItemResponses;
    OrderStatus status;

    Long userId;
    String username;
    String paymentMethod;

    PaymentStatus paymentStatus;

    String couponCode;
    BigDecimal discountAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;

}


