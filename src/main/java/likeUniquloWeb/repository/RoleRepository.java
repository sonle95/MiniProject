package likeUniquloWeb.repository;

import likeUniquloWeb.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository  extends JpaRepository<Role, String> {

    List<Role> findAllByNameIn(Set<String> names);

    boolean existsByName(String name);

    Optional<Role> findByName(String name);
}
