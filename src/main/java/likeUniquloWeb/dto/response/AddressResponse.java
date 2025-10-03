package likeUniquloWeb.dto.response;

import likeUniquloWeb.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {
    Long id;
    String street;
    String province;
    String district;
    String ward;
    String phone;
    String firstName;
    String lastName;
    boolean addressDefault = false;

}
