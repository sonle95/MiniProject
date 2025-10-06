package likeUniquloWeb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @Column(name = "token", unique = true, nullable = false)
    String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "expiry_date", nullable = false)
    LocalDate expiryDate;

    @Column(name = "created_at", nullable = false)
    LocalDate createdAt;

    @Column(name = "is_revoked", nullable = false)
    boolean isRevoked = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(this.expiryDate);
    }
}
