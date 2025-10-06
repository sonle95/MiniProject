package likeUniquloWeb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
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
    List<VariantResponse> productVariants;
    List<String> urls ;
    List<ImageResponse> images;
    List<ReviewResponse> reviews;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
}
