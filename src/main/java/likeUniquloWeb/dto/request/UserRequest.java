package likeUniquloWeb.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    String username;
    String password;
    String firstName;
    String lastName;
    String email;
    LocalDate dob;

}

