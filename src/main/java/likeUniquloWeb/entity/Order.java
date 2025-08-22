package likeUniquloWeb.entity;

import jakarta.persistence.*;
import likeUniquloWeb.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String orderNumber;
    BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    OrderStatus status = OrderStatus.PENDING;
    LocalDate createAt;
    LocalDate updateAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<OrderItems> orderItems;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    Coupon coupon;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDate.now();
        updateAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDate.now();
    }


}
