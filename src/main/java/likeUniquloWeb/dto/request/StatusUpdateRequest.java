package likeUniquloWeb.dto.request;

import jakarta.validation.constraints.NotNull;
import likeUniquloWeb.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatusUpdateRequest {
    @NotNull
    OrderStatus newStatus;
}
