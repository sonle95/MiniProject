package likeUniquloWeb.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {


    @NotNull(message = "userId is required")
    Long userId;

    String paymentMethod;

    Long couponId;
    @NotEmpty(message = "order items can not be empty")
    @Valid
    List<OrderItemRequest> orderItems;

}
