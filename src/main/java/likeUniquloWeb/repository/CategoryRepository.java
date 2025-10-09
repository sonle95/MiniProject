package likeUniquloWeb.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import likeUniquloWeb.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    Optional<Category> findByNameIgnoreCase(String name);

    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keySearch, '%'))")
    Page<Category> searchByName(@Param("keySearch") String keySearch, Pageable pageable);


    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
    Optional<Category> findByIdWithProducts(@Param("id") Long id);

    Category findByName(@NotBlank(message = "Category name is required") @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters") String name);
}


