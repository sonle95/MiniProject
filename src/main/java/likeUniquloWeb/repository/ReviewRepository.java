package likeUniquloWeb.repository;

import likeUniquloWeb.entity.Review;
import likeUniquloWeb.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select (count(r) > 0) from Review r")
    boolean existsByUserIdAndProductVariantId(Long userId, Long productVariantId);


    @Query("SELECT r FROM Review r WHERE r.product.id = :productId")
    List<Review> findByProduct_Id(Long productId);


    boolean existsByUser_IdAndProduct_Id(Long userId, Long productId);
}
