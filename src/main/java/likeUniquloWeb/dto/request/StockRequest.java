package likeUniquloWeb.dto.request;

import jakarta.validation.constraints.*;
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


    @PositiveOrZero(message = "Quantity cannot be negative")
    @Max(value = 99999, message = "Quantity cannot exceed 99999")
    int quantity;

    @NotBlank(message = "Warehouse code is required")
    @Size(max = 20, message = "Warehouse code cannot exceed 20 characters")
    String warehouseCode;

    @NotNull(message = "Product variant ID is required")
    Long productVariantId;
}
