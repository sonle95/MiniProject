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
public class UserUpdateRequest {
    String username;
    String password;
    String firstName;
    String lastName;
    LocalDate dob;
    Set<String> roles;
}
