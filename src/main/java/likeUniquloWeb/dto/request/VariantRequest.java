package likeUniquloWeb.dto.request;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VariantRequest {


    @NotBlank(message = "Size is required")
    @Size(max = 10, message = "Size cannot exceed 10 characters")
    String size;

    @NotBlank(message = "Color is required")
    @Size(max = 50, message = "Color cannot exceed 50 characters")
    String color;

    @NotNull
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid price format")
    BigDecimal price;


}
