package likeUniquloWeb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true , nullable = false, length = 100)
    String name;
    @Column(length = 500)
    String description;

    @Column(length = 500)
    String imageUrl;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    Set<Product> products;

}
