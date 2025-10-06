package likeUniquloWeb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Coupon extends BaseEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, unique = true, length = 50)
    String code;
    @Column(nullable = false, precision = 12, scale = 2)
    BigDecimal value;
    LocalDate validFrom;
    LocalDate validTo;
    int usageLimit;

    @OneToMany(mappedBy = "coupon", fetch = FetchType.LAZY)
    Set<Order> orders;
}
