package edu.hust.travelbookingsystem.service;

import edu.hust.travelbookingsystem.entity.Flight;
import edu.hust.travelbookingsystem.model.request.FlightDTO;

import java.util.List;

public interface FlightService {
    public Flight createFlight(FlightDTO flightDTO);
    public void deleteFlight(Long id);
    public Flight updateFlight(Long id,FlightDTO flightDTO);
    public List<Flight> getAllFlights();
}
