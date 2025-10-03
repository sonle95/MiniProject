package likeUniquloWeb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {

    Long id;
    String productName;
    Long productVariantId;
    String color;
    String size;
    String variantDetails;
    int quantity;
    BigDecimal unitPrice;
    BigDecimal totalPrice;

}
