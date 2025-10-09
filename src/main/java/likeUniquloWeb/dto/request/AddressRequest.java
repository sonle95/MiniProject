package likeUniquloWeb.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequest {

    String street;
    String province;
    String district;
    String ward;
    String phone;
    String firstName;
    String lastName;
    boolean addressDefault;
    Long userId;
}
