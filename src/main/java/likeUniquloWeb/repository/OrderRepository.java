package likeUniquloWeb.repository;

import jakarta.validation.constraints.NotNull;
import likeUniquloWeb.entity.Order;
import likeUniquloWeb.enums.OrderStatus;
import likeUniquloWeb.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByUser_IdAndOrderItems_ProductVariant_IdAndStatus(
            Long userId,
            Long productVariantId,
            OrderStatus status
    );
    @Query("""
        SELECT MONTH(o.createdAt), SUM(o.totalAmount)
        FROM Order o
        WHERE YEAR(o.createdAt) = :year
          AND o.paymentStatus = :status
        GROUP BY MONTH(o.createdAt)
        ORDER BY MONTH(o.createdAt)
        """)
    List<Object[]> getMonthlyRevenue(@Param("year") int year,
                                     @Param("status") PaymentStatus status);

    // ================================
    // 🟢 2. Doanh thu theo năm
    // ================================
    @Query("""
        SELECT YEAR(o.createdAt), SUM(o.totalAmount)
        FROM Order o
        WHERE o.paymentStatus = :status
        GROUP BY YEAR(o.createdAt)
        ORDER BY YEAR(o.createdAt)
        """)
    List<Object[]> getYearlyRevenue(@Param("status") PaymentStatus status);

    // ================================
    // 🟢 3. Top sản phẩm bán chạy
    // ================================
    @Query("""
        SELECT p.name, SUM(oi.quantity)
        FROM OrderItems oi
        JOIN oi.productVariant pv
        JOIN pv.product p
        JOIN oi.order o
        WHERE o.paymentStatus = :status
        GROUP BY p.name
        ORDER BY SUM(oi.quantity) DESC
        """)
    List<Object[]> getTopSellingProducts(@Param("status") PaymentStatus status);

    // ================================
    // 🟢 4. Top khách hàng chi tiêu nhiều nhất
    // ================================
    @Query("""
        SELECT o.user.username, SUM(o.totalAmount)
        FROM Order o
        WHERE o.paymentStatus = :status
        GROUP BY o.user.username
        ORDER BY SUM(o.totalAmount) DESC
        """)
    List<Object[]> getTopSpendingUsers(@Param("status") PaymentStatus status);


}
