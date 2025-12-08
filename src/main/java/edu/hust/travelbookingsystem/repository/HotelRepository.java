package edu.hust.travelbookingsystem.repository;

import edu.hust.travelbookingsystem.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    Optional<Hotel> findById(Long id);
    @Query(value = "SELECT * FROM hotels h WHERE REPLACE(h.address, ' ', '') LIKE CONCAT('%', REPLACE(:destination, ' ', ''), '%')", nativeQuery = true)
    List<Hotel> findByDestination(@Param("destination") String destination);
}
