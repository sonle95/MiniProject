package likeUniquloWeb.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {

    String token;
    String refreshToken;
    boolean authenticated;
    String tokenType = "Bearer";
    long expiresIn;
}
