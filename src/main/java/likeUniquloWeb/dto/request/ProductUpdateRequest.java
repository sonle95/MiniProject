package likeUniquloWeb.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdateRequest {
    @Size(max = 100, message = "SKU cannot exceed 100 characters")
    String stockKeepingUnit;

    @Size(min = 2, max = 200, message = "Product name must be between 2 and 200 characters")
    String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid price format")
    BigDecimal price;

    Boolean active;

    Long categoryId;


    @Valid
    List<VariantRequest> productVariants;


    @Size(max = 10, message = "Maximum 10 images allowed")
    List<MultipartFile> newImages;


    @Valid
    List<@NotNull(message = "Image ID cannot be null") @Positive Long> deleteImageIds;


    @Valid
    List<ReviewRequest> reviews;
}
