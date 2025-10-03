package likeUniquloWeb.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlideResponse {
    private Long id;
    private String imgUrl;
    private String title;
    boolean active;
}

