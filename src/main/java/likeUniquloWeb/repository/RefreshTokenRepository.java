package likeUniquloWeb.repository;

import jakarta.transaction.Transactional;
import likeUniquloWeb.entity.RefreshToken;
import likeUniquloWeb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByTokenAndIsRevokedFalse(String token);

    @Transactional
    @Modifying
    @Query("delete from RefreshToken rt where rt.user.id = :userId")
    void deleteByUserId(Long userId);


    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.user = :user")
    void revokeAllTokensByUser(@Param("user") User user);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDate now);

    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.user = :user AND rt.isRevoked = false")
    long countActiveTokensByUser(@Param("user") User user);
}
