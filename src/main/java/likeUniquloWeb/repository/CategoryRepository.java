package likeUniquloWeb.repository;

import likeUniquloWeb.entity.Category;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    Optional<Category> findByNameIgnoreCase(String name);

//    @Query("SELECT c FROM Category c JOIN FETCH c.products WHERE c.name = :name")
//    Optional<Category> findByNameWithProducts(@Param("name") String name);
}
