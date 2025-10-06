package likeUniquloWeb.repository;

import likeUniquloWeb.entity.Image;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("""
    SELECT i FROM Image i
    WHERE LOWER(i.product.name) LIKE LOWER(CONCAT('%', :keySearch, '%'))
       OR CAST(i.product.id AS string) LIKE CONCAT('%', :keySearch, '%')
        """)
    Page<Image> searchImagesByProductNameOrId(@Param("keySearch") String keySearch, Pageable pageable);


}
