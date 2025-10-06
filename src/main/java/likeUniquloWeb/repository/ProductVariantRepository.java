package likeUniquloWeb.repository;

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
}
