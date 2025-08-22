package likeUniquloWeb.repository;

import jdk.jfr.Registered;
import likeUniquloWeb.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItems, Long> {
}
