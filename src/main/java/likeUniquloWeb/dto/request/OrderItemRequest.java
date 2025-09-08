package likeUniquloWeb.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemRequest {

    @Positive(message = "Quantity must be positive")
    @Max(value = 999, message = "Quantity cannot exceed 999")
    int quantity;
    @NotNull(message = "Product variant ID is required")
    Long productVariantId;


}
