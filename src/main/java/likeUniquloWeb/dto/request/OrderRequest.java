package likeUniquloWeb.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {

    String paymentMethod;

    Long couponId;
    @NotEmpty(message = "order items can not be empty")
    @Valid
    List<OrderItemRequest> orderItems;

    @NotNull(message = "addressId is required for order placement")
    Long addressId;

}
