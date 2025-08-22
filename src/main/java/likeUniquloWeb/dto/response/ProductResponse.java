package likeUniquloWeb.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {

    Long id;
    String stockKeepingUnit;
    String name;
    String description;
    BigDecimal price;
    boolean active;
    String categoryName;
    List<VariantResponse> variantResponses;
    List<String> urls ;
    List<ReviewResponse> reviewResponses;
}
