package likeUniquloWeb.service;

import jakarta.transaction.Transactional;
import likeUniquloWeb.dto.request.UserRequest;
import likeUniquloWeb.dto.request.UserUpdateRequest;
import likeUniquloWeb.dto.response.UserResponse;
import likeUniquloWeb.entity.User;
import likeUniquloWeb.enums.Role;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.UserMapper;
import likeUniquloWeb.repository.RefreshTokenRepository;
import likeUniquloWeb.repository.RoleRepository;
import likeUniquloWeb.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    RefreshTokenRepository refreshTokenRepository;
    AuthenticationService authenticationService;

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUser(UserRequest request){
        if(userRepository.existsByUsername(request.getUsername())){
            throw  new AppException(ErrorCode.USER_EXISTED);
        }
        if(userRepository.existsByEmail(request.getEmail())){
            throw  new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Assign roles from request, or default to USER role
        if(request.getRoles() != null && !request.getRoles().isEmpty()){
            var roles = roleRepository.findAllByNameIn(request.getRoles());
            if(roles.isEmpty()){
                // If no valid roles found, assign USER role
                var userRole = roleRepository.findByName("USER")
                        .orElseGet(() -> {
                            likeUniquloWeb.entity.Role role = new likeUniquloWeb.entity.Role();
                            role.setName("USER");
                            return roleRepository.save(role);
                        });
                user.setRoles(new HashSet<>(List.of(userRole)));
            } else {
                user.setRoles(new HashSet<>(roles));
            }
        } else {
            // Default to USER role if no roles specified
            var userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> {
                        likeUniquloWeb.entity.Role role = new likeUniquloWeb.entity.Role();
                        role.setName("USER");
                        return roleRepository.save(role);
                    });
            user.setRoles(new HashSet<>(List.of(userRole)));
        }

        log.info("✅ Creating user {} with roles: {}", user.getUsername(), user.getRoles().stream().map(likeUniquloWeb.entity.Role::getName).toList());

        return userMapper.toDto(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll(){
        return userRepository.findAll()
                .stream().map(userMapper::toDto).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public UserResponse updateUser(Long userId ,UserUpdateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.update(request, user);

        // Only update password if a new password is provided
        if(request.getPassword() != null && !request.getPassword().trim().isEmpty()){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            log.info("🔐 Updating password for user: {}", user.getUsername());
        }

        // Update roles if provided
        if(request.getRoles() != null && !request.getRoles().isEmpty()){
            var roles = roleRepository.findAllByNameIn(request.getRoles());
            user.setRoles(new HashSet<>(roles));
            log.info("🛡️ Updating roles for user {} to: {}", user.getUsername(), request.getRoles());
        }

        return userMapper.toDto(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void delete(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }

    public UserResponse findUserById(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toDto(user);
    }


    public Page<UserResponse> getUsersByPageAndSearch(int page, int size, String keySearch, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by("id").ascending()
                : Sort.by("id").descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> users;
        if (keySearch == null || keySearch.trim().isEmpty()) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.searchByUserName(keySearch.trim(), pageable);
        }

        return users.map(userMapper::toDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public UserResponse getMyInfo(String token) {
        User user = authenticationService.getUserFromToken(token);
        return userMapper.toDto(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponse toggleUserActive(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setActive(!user.isActive());
        User updatedUser = userRepository.save(user);

        log.info("🔄 User {} active status changed to: {}", user.getUsername(), user.isActive());

        return userMapper.toDto(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponse setUserActive(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setActive(active);
        User updatedUser = userRepository.save(user);

        log.info("✅ User {} active status set to: {}", user.getUsername(), active);

        return userMapper.toDto(updatedUser);
    }

}
