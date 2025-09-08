package likeUniquloWeb.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CartResponse {

    Long id;
    String sessionId;
    List<CartItemResponse> items;
    BigDecimal totalAmount;
    int totalItems;
    LocalDateTime expiresAt;
}
