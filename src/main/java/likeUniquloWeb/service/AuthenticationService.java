package likeUniquloWeb.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import likeUniquloWeb.dto.request.AuthenticationRequest;
import likeUniquloWeb.dto.request.RefreshTokenRequest;
import likeUniquloWeb.dto.request.RegistrationRequest;
import likeUniquloWeb.dto.response.AuthenticationResponse;
import likeUniquloWeb.dto.response.UserResponse;
import likeUniquloWeb.entity.RefreshToken;
import likeUniquloWeb.entity.Role;
import likeUniquloWeb.entity.User;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.RegistrationMapper;
import likeUniquloWeb.mapper.UserMapper;
import likeUniquloWeb.repository.RefreshTokenRepository;
import likeUniquloWeb.repository.RoleRepository;
import likeUniquloWeb.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RegistrationMapper registrationMapper;
    RoleRepository roleRepository;
    RefreshTokenRepository refreshTokenRepository;
    MailService mailService;

    @Value("${jwt.signer.key}")
    @NonFinal
    String singerKey;

    @Value("${jwt.expiration.hours:1}")
    @NonFinal
    int tokenExpirationHours;

    @Value("${jwt.refresh-token.expiration.days:30}")
    @NonFinal
    int refreshTokenExpirationDays;

    @Value("${jwt.issuer:likeUniqulo-app}")
    @NonFinal
    String tokenIssuer;
    @Value("${jwt.access-token.expiration.hours:1}")
    @NonFinal
    int accessTokenExpirationHours;

    private static final int MAX_REFRESH_TOKENS_PER_USER = 5;


    public UserResponse register(RegistrationRequest request){
        if(userRepository.existsByUsername(request.getUsername())){
            throw  new AppException(ErrorCode.USER_EXISTED);
        }
        if(userRepository.existsByEmail(request.getEmail())){
            throw  new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        User user = registrationMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("USER");
                    return roleRepository.save(role);
                });
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);

        if (request.getRoles() != null && !request.getRoles().isEmpty()){
            var roles = roleRepository.findAllByNameIn(request.getRoles());
//            user.setRoles(new HashSet<>(roles));
            userRoles.addAll(new HashSet<>(roles));

        }
        user.setRoles(userRoles);
        User savedUser = userRepository.save(user);

        mailService.sendTextMail(
                user.getEmail(),
                "Welcome to LikeUniqloWeb 🎉",
                "Xin chào " + user.getUsername() + ",\n\n" +
                        "Bạn đã đăng ký thành công tài khoản tại LikeUniqloWeb!"
        );

        return userMapper.toDto(savedUser);
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request){
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!authenticated){
            throw  new AppException(ErrorCode.PASSWORD_WRONG);
        }
        cleanupRefreshTokens(user);
        var token = generateToken(user);
        String refreshToken = generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .expiresIn(tokenExpirationHours*3600L)
                .token(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    @Transactional
    public AuthenticationResponse createRefreshToken(RefreshTokenRequest request){
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndIsRevokedFalse(request.getRefreshToken())
                .orElseThrow(()-> new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if(refreshToken.isExpired()){
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        User user = refreshToken.getUser();
        var newToken = generateToken(user);
        String newRefreshToken = generateRefreshToken(user);
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .tokenType("Bearer")
                .token(newToken)
                .refreshToken(newRefreshToken)
                .expiresIn(accessTokenExpirationHours * 3600L)
                .build();
    }

    @Transactional
    public void logout(String refreshTokenValue){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(()-> new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void logoutAllDevices(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        refreshTokenRepository.revokeAllTokensByUser(user);
    }

    private String generateRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDate.now().plusDays(refreshTokenExpirationDays));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    @Transactional(readOnly = true)
    public User getUserFromToken(String token) {
        try {

            String actualToken = token.replace("Bearer ", "");


            JWSObject jwsObject = JWSObject.parse(actualToken);



            if (!jwsObject.verify(new MACVerifier(singerKey.getBytes()))) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }


            Map<String, Object> claims = jwsObject.getPayload().toJSONObject();


            String username = (String) claims.get("sub");
            if (username == null) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }


            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }



    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer(tokenIssuer)
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(tokenExpirationHours, ChronoUnit.HOURS)))
                .claim("scope", buildScope(user))
                .claim("userId", user.getId())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        try{
            jwsObject.sign(new MACSigner(singerKey.getBytes()));
            return jwsObject.serialize();
        }catch (JOSEException e){
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if(!CollectionUtils.isEmpty(role.getPermissions())){
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add(permission.getName());
                    });
                }
            });
        }
        return stringJoiner.toString();
    }

    private void cleanupRefreshTokens(User user) {
        long activeTokenCount = refreshTokenRepository.countActiveTokensByUser(user);

        if (activeTokenCount >= MAX_REFRESH_TOKENS_PER_USER) {
            refreshTokenRepository.revokeAllTokensByUser(user);
        }
    }

    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDate.now());
    }
}
