package likeUniquloWeb.repository;

import likeUniquloWeb.entity.Address;
import likeUniquloWeb.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;
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





}
