package likeUniquloWeb.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckoutRequest {
    @NotNull(message = "addressId is required for checkout")
    Long addressId;

    @NotBlank(message = "payment method is required")
    String paymentMethod;

    Long couponId;
}
