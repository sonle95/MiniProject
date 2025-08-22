package likeUniquloWeb.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String stockKeepingUnit;
    String name;
    String description;
    BigDecimal price;
    boolean active;
    LocalDate createAt;
    LocalDate updateAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDate.now();
        updateAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDate.now();
    }

    @OneToMany(mappedBy = "product",  cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<ProductVariant> productVariants;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<Review> reviews;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    List<Image> images;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<OrderItems> orderItems;


}
