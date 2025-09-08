package likeUniquloWeb.entity;

import jakarta.persistence.*;
import likeUniquloWeb.enums.OrderStatus;
import likeUniquloWeb.enums.PaymentStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "orders")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 50)
    String orderNumber;

    @Column(precision = 14, scale = 2)
    BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    OrderStatus status = OrderStatus.PENDING;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    String paymentMethod;

    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<OrderItems> orderItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    Coupon coupon;

    @PrePersist
    protected void onCreate() {
        if (orderNumber == null || orderNumber.isEmpty()) {
            orderNumber = generateOrderNumber();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }


}
