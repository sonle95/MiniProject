package likeUniquloWeb.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    String name;
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    String imageUrl;
}
