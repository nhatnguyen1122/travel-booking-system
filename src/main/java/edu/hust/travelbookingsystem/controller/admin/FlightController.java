package edu.hust.travelbookingsystem.controller.admin;

import edu.hust.travelbookingsystem.entity.Flight;
import edu.hust.travelbookingsystem.model.request.FlightDTO;
import edu.hust.travelbookingsystem.model.response.ApiResponse;
import edu.hust.travelbookingsystem.service.FlightService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flight")
@Slf4j
public class FlightController {
    @Autowired
    private FlightService flightService;
    @PostMapping("/create")
    public ApiResponse<Flight> createFlight(@Valid @RequestBody FlightDTO flightDTO) {
        log.info("Create flightDTO: {}", flightDTO);
        ApiResponse<Flight> apiResponse = new ApiResponse<>();
        Flight flight = flightService.createFlight(flightDTO);
        apiResponse.setData(flight);
        apiResponse.setMessage("Flight created");
        log.info("Flight created successfully: {}", flight);
        return apiResponse;
    }
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Flight> deleteFlight(@PathVariable Long id) {
        log.info("Delete flight id = : {}", id);
        ApiResponse<Flight> apiResponse = new ApiResponse<>();
        flightService.deleteFlight(id);
        apiResponse.setMessage("Flight deleted");
        log.info("Flight deleted successfully id = : {}", id);
        return apiResponse;
    }
    @PatchMapping("/update/{id}")
    public ApiResponse<Flight> updateFlight(@PathVariable Long id,@Valid @RequestBody FlightDTO flightDTO) {
        log.info("Update flight id = {}", id);
        ApiResponse<Flight> apiResponse = new ApiResponse<>();
        apiResponse.setData(flightService.updateFlight(id, flightDTO));
        apiResponse.setMessage("Flight updated");
        log.info("Flight updated successfully id = {}", id);
        return apiResponse;
    }
    @GetMapping("/getAll")
    public ApiResponse<List<Flight>> getAllFlights() {
        log.info("Get all flights");
        ApiResponse<List<Flight>> apiResponse = new ApiResponse<>();
        apiResponse.setData(flightService.getAllFlights());
        apiResponse.setMessage("success");
        log.info("Get all success");
        return apiResponse;
    }
}
