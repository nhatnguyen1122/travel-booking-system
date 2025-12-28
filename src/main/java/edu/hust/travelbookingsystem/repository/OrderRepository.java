package edu.hust.travelbookingsystem.repository;

import edu.hust.travelbookingsystem.entity.Flight;
import edu.hust.travelbookingsystem.entity.Order;
import edu.hust.travelbookingsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    Page<Order> findByUser(User user, Pageable pageable);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    List<Order> findByFlight(Flight flight);
}
