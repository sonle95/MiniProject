package likeUniquloWeb.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    String stockKeepingUnit;
    String name;
    String description;
    BigDecimal price;
    boolean active;
    Long categoryId;
    List<VariantRequest> variantRequests;
    List<MultipartFile> images ;
    List<ReviewRequest> reviewRequests;



}
