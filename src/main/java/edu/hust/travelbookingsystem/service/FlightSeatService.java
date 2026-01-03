package edu.hust.travelbookingsystem.service;

import edu.hust.travelbookingsystem.entity.Flight;
import edu.hust.travelbookingsystem.entity.FlightSeat;
import edu.hust.travelbookingsystem.entity.Order;

import java.util.List;

public interface FlightSeatService {
    void initializeSeatsForFlight(Long flightId, int numberOfSeats);
    List<FlightSeat> getAvailableSeats(Long flightId);
    List<FlightSeat> getAllSeats(Long flightId);
    boolean bookSeats(Flight flight, Order order, List<String> seatNumbers);
    void releaseSeats(Long orderId);
    long getAvailableSeatCount(Long flightId);
}
