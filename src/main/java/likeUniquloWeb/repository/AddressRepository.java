package likeUniquloWeb.repository;

import likeUniquloWeb.entity.Address;
import likeUniquloWeb.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);

    @Modifying
    @Query("UPDATE Address a SET a.addressDefault = false WHERE a.user = :user AND a.id <> :addressId")
    void clearDefaultAddress(User user, Long addressId);

    List<Address> findByUserId(Long userId);

    Optional<Address> findByUserIdAndAddressDefaultTrue(Long userId);


    @Query("""
    SELECT a FROM Address a
    WHERE LOWER(a.user.username) LIKE LOWER(CONCAT('%', :keySearch, '%'))
       OR LOWER(a.user.email) LIKE LOWER(CONCAT('%', :keySearch, '%'))
    """)
    Page<Address> searchByUserKeyword(@Param("keySearch") String keySearch, Pageable pageable);

    List<Address> findByUser_Id(Long userId);

    List<Address> findByUser_IdAndAddressDefaultTrue(Long userId);

    long countByUser_Id(Long userId);


}
