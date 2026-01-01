package edu.hust.travelbookingsystem.controller.admin;

import edu.hust.travelbookingsystem.entity.Hotel;
import edu.hust.travelbookingsystem.entity.Order;
import edu.hust.travelbookingsystem.model.request.HotelDTO;
import edu.hust.travelbookingsystem.model.response.ApiResponse;
import edu.hust.travelbookingsystem.repository.OrderRepository;
import edu.hust.travelbookingsystem.service.HotelService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
public class HotelController {
    @Autowired
    private HotelService hotelService;
    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/createHotel")
    public ApiResponse<Hotel> createHotel(@Valid @RequestBody HotelDTO hotelDTO) {
        log.info("Create hotelDTO: {}", hotelDTO);
        ApiResponse<Hotel> apiResponse = new ApiResponse<>();
        apiResponse.setData(hotelService.createHotel(hotelDTO));
        log.info("Created hotel successfully: {}", apiResponse.getData());
        return apiResponse;
    }
    @GetMapping("/getAllHotels")
    public ApiResponse<List<Hotel>> getAllHotels() {
        log.info(("Get all hotels "));
        ApiResponse<List<Hotel>> apiResponse = new ApiResponse<>();
        apiResponse.setData(hotelService.getAllHotels());
        apiResponse.setMessage("Success");
        log.info("Get all hotels successfully: {}", apiResponse.getData());
        return apiResponse;
    }
    @GetMapping("/hotel-in-destination")
    public ApiResponse<List<Hotel>> getHotelInDestination(@RequestParam Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new RuntimeException("Order not found"));
        String destination = order.getDestination();
        ApiResponse<List<Hotel>> apiResponse = new ApiResponse<>();
        apiResponse.setData(hotelService.getHotelsByDestination(destination));
        apiResponse.setMessage("Success");
        log.info("Get hotels successfully: {}", apiResponse.getData());
        return apiResponse;
    }
    @PutMapping("/updateHotel/{id}")
    public  ApiResponse<Hotel> updateHotel(@Valid @RequestBody HotelDTO hotelDTO,@PathVariable Long id) {
        log.info("Update hotelDTO id =  : {}", id);
        ApiResponse<Hotel> apiResponse = new ApiResponse<>();
        apiResponse.setData(hotelService.updateHotel(hotelDTO,id));
        log.info("Update hotel successfully id = : {}", id);
        return apiResponse;
    }
    @DeleteMapping("/{id}")
    public ApiResponse<Hotel> deleteHotel(@PathVariable Long id) {
        log.info("Delete hotel id =  : {}", id);
        ApiResponse<Hotel> apiResponse = new ApiResponse<>();
        hotelService.deleteHotel(id);
        apiResponse.setMessage("Delete Success");
        log.info("Delete hotel successfully id = : {}", id);
        return apiResponse;
    }
}
