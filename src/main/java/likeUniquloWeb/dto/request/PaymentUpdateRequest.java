package likeUniquloWeb.dto.request;

import likeUniquloWeb.enums.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentUpdateRequest {

    PaymentStatus paymentStatus;
}
