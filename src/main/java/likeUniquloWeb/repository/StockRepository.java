package likeUniquloWeb.repository;

import likeUniquloWeb.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProductVariant_Id(Long productVariantId);
    List<Stock> findByProductVariant_IdIn(List<Long> productVariantIds);
}
