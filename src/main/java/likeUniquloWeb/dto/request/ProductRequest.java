package likeUniquloWeb.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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

    @Size(max = 100, message = "SKU cannot exceed 100 characters")
    String stockKeepingUnit;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 200, message = "Product name must be between 2 and 200 characters")
    String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid price format")
    BigDecimal price;
    boolean active = true;

    @NotNull(message = "Category ID is required")
    Long categoryId;

    @Valid
    List<VariantRequest> productVariants;

    @Size(max = 10, message = "Maximum 10 images allowed")
    List<MultipartFile> images ;

    @Valid
    List<ReviewRequest> reviews;



}
