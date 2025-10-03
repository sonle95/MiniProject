package likeUniquloWeb.configuration;

import likeUniquloWeb.entity.Role;
import likeUniquloWeb.entity.User;
import likeUniquloWeb.repository.RoleRepository;
import likeUniquloWeb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public void run(String... args) {

        var adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    var role = new Role();
                    role.setName("ADMIN");
                    return roleRepository.save(role);
                });


        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);

            log.info("✅ Default admin user created: username=admin, password=admin123");
        }
    }
}


