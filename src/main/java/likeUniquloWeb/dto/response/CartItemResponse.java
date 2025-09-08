package likeUniquloWeb.dto.response;

import java.math.BigDecimal;

public class CartItemResponse {

    Long id;
    String productName;
    Long productVariantId;
    String variantDetails;
    int quantity;
    BigDecimal unitPrice;
    BigDecimal totalPrice;
}
