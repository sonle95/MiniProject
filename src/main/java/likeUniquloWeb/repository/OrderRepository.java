package likeUniquloWeb.repository;

import jakarta.validation.constraints.NotNull;
import likeUniquloWeb.entity.Order;
import likeUniquloWeb.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByUser_IdAndOrderItems_ProductVariant_IdAndStatus(
            Long userId,
            Long productVariantId,
            OrderStatus status
    );

}
