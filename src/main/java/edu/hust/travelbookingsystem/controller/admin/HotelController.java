package edu.hust.travelbookingsystem.controller.admin;

import edu.hust.travelbookingsystem.entity.Hotel;
import edu.hust.travelbookingsystem.entity.Order;
import edu.hust.travelbookingsystem.model.request.HotelDTO;
import edu.hust.travelbookingsystem.model.response.ApiReponse;
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
    public ApiReponse<Hotel> createHotel(@Valid @RequestBody HotelDTO hotelDTO) {
        log.info("Create hotelDTO: {}", hotelDTO);
        ApiReponse<Hotel> apiReponse = new ApiReponse<>();
        apiReponse.setData(hotelService.createHotel(hotelDTO));
        log.info("Created hotel successfully: {}", apiReponse.getData());
        return apiReponse;
    }
    @GetMapping("/getAllHotels")
    public ApiReponse<List<Hotel>> getAllHotels() {
        log.info(("Get all hotels "));
        ApiReponse<List<Hotel>> apiReponse = new ApiReponse<>();
        apiReponse.setData(hotelService.getAllHotels());
        apiReponse.setMessage("Success");
        log.info("Get all hotels successfully: {}", apiReponse.getData());
        return apiReponse;
    }
    @GetMapping("/hotel-in-destination")
    public ApiReponse<List<Hotel>> getHotelInDestination(@RequestParam Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new RuntimeException("Order not found"));
        String destination = order.getDestination();
        ApiReponse<List<Hotel>> apiReponse = new ApiReponse<>();
        apiReponse.setData(hotelService.getHotelsByDestination(destination));
        apiReponse.setMessage("Success");
        log.info("Get hotels successfully: {}", apiReponse.getData());
        return apiReponse;
    }
    @PutMapping("/updateHotel/{id}")
    public  ApiReponse<Hotel> updateHotel(@Valid @RequestBody HotelDTO hotelDTO,@PathVariable Long id) {
        log.info("Update hotelDTO id =  : {}", id);
        ApiReponse<Hotel> apiReponse = new ApiReponse<>();
        apiReponse.setData(hotelService.updateHotel(hotelDTO,id));
        log.info("Update hotel successfully id = : {}", id);
        return apiReponse;
    }
    @DeleteMapping("/{id}")
    public ApiReponse<Hotel> deleteHotel(@PathVariable Long id) {
        log.info("Delete hotel id =  : {}", id);
        ApiReponse<Hotel> apiReponse = new ApiReponse<>();
        hotelService.deleteHotel(id);
        apiReponse.setMessage("Delete Success");
        log.info("Delete hotel successfully id = : {}", id);
        return apiReponse;
    }
}
