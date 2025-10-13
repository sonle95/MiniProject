package likeUniquloWeb.configuration;

import likeUniquloWeb.entity.Role;
import likeUniquloWeb.entity.User;
import likeUniquloWeb.repository.RoleRepository;
import likeUniquloWeb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.admin.auto-create:true}")
    private boolean autoCreate;

    @Override
    public void run(String... args) {
        if (!autoCreate) {
            log.info("⏭️  Auto-create admin is disabled");
            return;
        }

        // Create ADMIN role if not exists
        var adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    var role = new Role();
                    role.setName("ADMIN");
                    role.setDescription("System administrator with full access");
                    return roleRepository.save(role);
                });

        // Create USER role if not exists
        roleRepository.findByName("USER")
                .orElseGet(() -> {
                    var role = new Role();
                    role.setName("USER");
                    role.setDescription("Regular user with basic access");
                    return roleRepository.save(role);
                });

        // Create default admin user if not exists
        if (!userRepository.existsByUsername(adminUsername)) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);

            log.info("✅ Default admin user created: username={}, password={}", adminUsername, adminPassword);
            log.warn("⚠️  Remember to change the password in production!");
        } else {
            log.info("✅ Admin user '{}' already exists", adminUsername);
        }
    }
}


