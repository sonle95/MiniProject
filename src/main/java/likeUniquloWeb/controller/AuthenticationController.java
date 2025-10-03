package likeUniquloWeb.controller;

import jakarta.validation.Valid;
import likeUniquloWeb.dto.request.AuthenticationRequest;
import likeUniquloWeb.dto.request.RefreshTokenRequest;
import likeUniquloWeb.dto.request.RegistrationRequest;
import likeUniquloWeb.dto.response.ApiResponse;
import likeUniquloWeb.dto.response.AuthenticationResponse;
import likeUniquloWeb.dto.response.UserResponse;
import likeUniquloWeb.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@CrossOrigin(origins = "*")
public class AuthenticationController {
    AuthenticationService authenticationService;
    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegistrationRequest request) {
        log.info("Registration request for username: {}", request.getUsername());

        UserResponse userResponse = authenticationService.register(request);

        return userResponse;
    }

    @PostMapping("/login")
    public AuthenticationResponse authenticate(@Valid @RequestBody AuthenticationRequest request) {
        log.info("Authentication request for username: {}", request.getUsername());

        AuthenticationResponse authResponse = authenticationService.authenticate(request);

        return authResponse;
    }

    @PostMapping("/refresh")
    public AuthenticationResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh request");

        AuthenticationResponse authResponse = authenticationService.createRefreshToken(request);

        return authResponse;
    }

    @PostMapping("/logout")
    public String logout(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Logout request");

        authenticationService.logout(request.getRefreshToken());

        return "successful";
    }

    @PostMapping("/logout-all")
    public String logoutAllDevices(Authentication authentication) {
        log.info("Logout all devices request for user: {}", authentication.getName());

        authenticationService.logoutAllDevices(authentication.getName());

        return "successful";
    }

    @GetMapping("/me")
    public String getCurrentUser(Authentication authentication) {
        return "successful";
    }
}
