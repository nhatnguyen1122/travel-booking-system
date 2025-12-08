package edu.hust.travelbookingsystem.repository;

import edu.hust.travelbookingsystem.entity.Payment;
import edu.hust.travelbookingsystem.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findByStatus(PaymentStatus status);
}
