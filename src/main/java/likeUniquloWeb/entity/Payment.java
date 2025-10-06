package likeUniquloWeb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    BigDecimal amount;

    @Column(nullable = false, length = 20)
    String method;

    @Column(nullable = false, length = 20)
    String status;

    LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    Order order;
}
