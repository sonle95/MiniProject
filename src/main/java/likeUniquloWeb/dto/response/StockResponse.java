package likeUniquloWeb.dto.response;

import likeUniquloWeb.entity.ProductVariant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockResponse {

    Long id;
    int quantity;
    String warehouseCode;
    Long productVariantId;
    String productName;

}
