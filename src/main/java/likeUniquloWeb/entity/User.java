package likeUniquloWeb.entity;

import jakarta.persistence.*;
import likeUniquloWeb.enums.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String firstName;
    String lastName;

    @Column(nullable = false, unique = true, length = 150)
    String username;
    @Column(nullable = false)
    String password;
    @Column(nullable = false, unique = true, length = 255)
    String email;
    @Column(length = 30)
    String phone;

    boolean emailVerified;
    boolean active;

    LocalDate createAt;
    LocalDate updateAt;

    @Column(name = "is_admin", nullable = false)
    boolean admin;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY ,cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Address> addresses;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Review> reviews;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Order> orders;

    @Enumerated(EnumType.STRING)
    Role role;
}
