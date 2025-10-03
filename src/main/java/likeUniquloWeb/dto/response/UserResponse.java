package likeUniquloWeb.dto.response;

import likeUniquloWeb.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    Long id;
    String username;
    String password;
    String firstName;
    String lastName;
    String email;
    LocalDate dob;

    Set<RoleResponse> roles;
}
