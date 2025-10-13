package likeUniquloWeb.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import likeUniquloWeb.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    @Query("SELECT v FROM ProductVariant v JOIN v.stock s ORDER BY s.quantity ASC")
    Page<ProductVariant> findAllOrderByStockQuantityAsc(Pageable pageable);

    @Query("SELECT v FROM ProductVariant v JOIN v.stock s ORDER BY s.quantity DESC")
    Page<ProductVariant> findAllOrderByStockQuantityDesc(Pageable pageable);

    boolean existsByProductIdAndColorAndSize(@NotNull Long productId, @NotBlank(message = "Color is required") @Size(max = 50, message = "Color cannot exceed 50 characters") String color, @NotBlank(message = "Size is required") @Size(max = 10, message = "Size cannot exceed 10 characters") String size);

    boolean existsByProductIdAndColorAndSizeAndIdNot(Long id, @NotBlank(message = "Color is required") @Size(max = 50, message = "Color cannot exceed 50 characters") String color, @NotBlank(message = "Size is required") @Size(max = 10, message = "Size cannot exceed 10 characters") String size, Long id1);
}
