package likeUniquloWeb.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    String username;
    String password;
    String confirmPassword;
    String firstName;
    String lastName;
    String email;
    LocalDate dob;
    Set<String> roles;

}

