package likeUniquloWeb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 10, nullable = false)
    String size;

    @Column(length = 50, nullable = false)
    String color;

    @Column(precision = 12, scale = 2)
    BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    @OneToOne(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Stock stock;

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<OrderItems> orderItems;

    public void setStock(Stock stock) {
        this.stock = stock;
        if (stock != null) {
            stock.setProductVariant(this);
        }
    }
}
