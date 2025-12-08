package edu.hust.travelbookingsystem.repository;

import edu.hust.travelbookingsystem.entity.HotelBooking;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface HotelBookingRepository extends JpaRepository<HotelBooking, Long> {
    @Query("Select hb from HotelBooking hb where hb.hotel.id = :hotelId "
                                        + " and hb.hotelBedroom.id = :hotelBedroomId"
                                        +" and ( :startDate between hb.startDate and hb.endDate or :endDate between hb.startDate and hb.endDate)   "
    )
    List<HotelBooking> findOverLappingBookings(Long hotelId ,
                                               Long hotelBedroomId ,
                                               Date startDate , Date endDate);
    @Modifying
    @Transactional
    @Query("delete from HotelBooking  hb where hb.order.id = :orderId")
    void deleteByOrderId(Long orderId);
}
