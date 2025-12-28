package edu.hust.travelbookingsystem.repository;

import edu.hust.travelbookingsystem.entity.HotelBedroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelBedroomRepository extends JpaRepository<HotelBedroom, Long> {
}
