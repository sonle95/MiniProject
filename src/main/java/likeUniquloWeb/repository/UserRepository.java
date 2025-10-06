package likeUniquloWeb.repository;

import likeUniquloWeb.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query("""
        SELECT u FROM User u 
        WHERE LOWER(CONCAT(u.firstName, ' ', u.lastName)) 
              LIKE LOWER(CONCAT('%', :keySearch, '%'))
    """)
    Page<User> searchByUserName(@Param("keySearch") String keySearch, Pageable pageable);

}
