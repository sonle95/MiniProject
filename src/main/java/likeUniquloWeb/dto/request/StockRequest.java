package likeUniquloWeb.dto.request;

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
public class StockRequest {

    int quantity;
    String warehouseCode;
    ProductVariant variant;
}
