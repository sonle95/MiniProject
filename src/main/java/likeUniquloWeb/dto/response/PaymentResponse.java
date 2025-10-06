package likeUniquloWeb.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    Long id;
    BigDecimal amount;
    String method;
    String status;
    LocalDateTime paymentDate;
    Long orderId;
}
