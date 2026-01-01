package edu.hust.travelbookingsystem.repository;

import edu.hust.travelbookingsystem.entity.PasswordResetToken;
import edu.hust.travelbookingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUserAndUsedFalse(User user);

    void deleteByExpiryDateBefore(Date date);
}
