package likeUniquloWeb.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {

    Long id;
    String sessionId;
    List<CartItemResponse> cartItems;
    BigDecimal totalAmount;
    int totalItems;
    LocalDateTime expiresAt;
}
