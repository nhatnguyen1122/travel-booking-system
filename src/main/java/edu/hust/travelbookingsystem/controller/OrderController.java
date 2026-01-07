package edu.hust.travelbookingsystem.controller;

import edu.hust.travelbookingsystem.entity.Order;
import edu.hust.travelbookingsystem.model.request.OrderDTO;
import edu.hust.travelbookingsystem.model.request.OrderHotelDTO;
import edu.hust.travelbookingsystem.model.response.ApiResponse;
import edu.hust.travelbookingsystem.model.response.PageResponse;
import edu.hust.travelbookingsystem.repository.OrderRepository;
import edu.hust.travelbookingsystem.service.OrderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @PostMapping("/create/{id}") // id này là của user
    public ApiResponse<Order> addOrder(@Valid  @RequestBody OrderDTO orderDTO,@PathVariable Long id) {
        log.info("Start add order of user id = {}",id);
        ApiResponse<Order> apiResponse = new ApiResponse<>();
        apiResponse.setData(orderService.addOrder(orderDTO,id));
        log.info("Add order successfully of user id = {}",id);
        return apiResponse;
    }
    @PostMapping("/chooseHotel/{orderId}/{hotelId}") // id này là của id order dto
    public ApiResponse<Order> chooseHotel(
            @PathVariable Long orderId,
            @PathVariable Long hotelId,
            @RequestBody OrderHotelDTO orderHotelDTO) {
        log.info("Start choose hotel of user id = {}",orderId);

        ApiResponse<Order> apiResponse = new ApiResponse<>();

        Order order = orderService.chooseHotel(orderId, hotelId, orderHotelDTO);
        orderService.saveOrder(order);

        apiResponse.setData(order);
        apiResponse.setMessage("success");
        log.info("Choose hotel successfully of user id = {}",orderId);
        return apiResponse;
    }
    @PostMapping("/chooseFlight/{idOrder}/{idFlight}")
    public ApiResponse<Order> chooseFlight(@PathVariable Long idOrder,@PathVariable Long idFlight) {
        log.info("Start choose flight of user id = {}",idOrder);
        ApiResponse<Order> apiResponse = new ApiResponse<>();
        Order order = orderService.chooseFlight(idOrder,idFlight);
        apiResponse.setData(order);
        apiResponse.setMessage("success");
        log.info("Choose flight successfully of user id = {}",idOrder);
        return apiResponse;
    }

    @PostMapping("/chooseFlightWithSeats/{orderId}/{flightId}")
    public ApiResponse<Order> chooseFlightWithSeats(
            @PathVariable Long orderId,
            @PathVariable Long flightId,
            @RequestBody List<String> seatNumbers) {
        log.info("Start choose flight with seats for order id = {}", orderId);
        try {
            Order order = orderService.chooseFlightWithSeats(orderId, flightId, seatNumbers);
            return new ApiResponse<>(1000, "Flight and seats selected successfully", order);
        } catch (Exception e) {
            log.error("Error choosing flight with seats: {}", e.getMessage());
            return new ApiResponse<>(9999, e.getMessage(), null);
        }
    }

    @GetMapping("/single/{orderId}")
    public ApiResponse<Order> getOrderByIdSingle(@PathVariable Long orderId) {
        log.info("Start get single order id = {}", orderId);
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            return new ApiResponse<>(1000, "Get order success", order);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ApiResponse<>(9999, e.getMessage(), null);
        }
    
    // hủy path
    @DeleteMapping("/{id}")
    public ApiResponse<Order> deleteOrder(@PathVariable Long id) {
        log.info("Start delete order of user id = {}",id);
        ApiResponse<Order> apiResponse = new ApiResponse<>();
        orderService.cancelOrder(id);
        apiResponse.setMessage("cancel success");
        log.info("Delete order successfully of user id = {}",id);
        return apiResponse;
    }
    // cancel flight
    @PutMapping("/cancelFlight/{id}")
    public ApiResponse<Order> cancelFlight(@PathVariable Long id) {
        log.info("Start cancel flight of user id = {}",id);
        ApiResponse<Order> apiResponse = new ApiResponse<>();
        Order order = orderService.cancelFlight(id);
        apiResponse.setData(order);
        apiResponse.setMessage("cancel success");
        log.info("Cancel flight successfully of user id = {}",id);
        return apiResponse;
    }
    @GetMapping("/{id}")
    public ApiResponse<PageResponse> getOrderById(@PathVariable Long id, @RequestParam(defaultValue = "0",required = false) int pageNo,
                                                  @RequestParam(defaultValue = "5",required = false) int pageSize) {
        log.info("Start get order of user id = {}",id);
        try{
            PageResponse<?> orders = orderService.getOrdersByUserId(id,pageNo,pageSize);
            return new ApiResponse<>(1000,"get order by id success " , orders) ;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return new ApiResponse<>(7777,e.getMessage(),null);
        }
    }
    @GetMapping("/getAllOrder")
    public ApiResponse<PageResponse> getAllOrder(@RequestParam(defaultValue = "0",required = false) int pageNo,
                                                 @RequestParam(defaultValue = "5",required = false) int pageSize,
                                                 @RequestParam(required = false) String sortBy) {
        log.info("Start get order : {}",pageNo);
        try{
            PageResponse<?> orders = orderService.getAllOrders(pageNo,pageSize,sortBy)  ;
            return new ApiResponse<>(1000,"get success",orders);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ApiResponse<>(7777,e.getMessage(),null);
        }
    }
    @GetMapping("/getAllOrderWithMultipleColumns")
    public ApiResponse<PageResponse> getAllOrderWithSortByMultipleColums(@RequestParam(defaultValue = "0",required = false) int pageNo,
                                                                         @RequestParam(defaultValue = "5",required = false) int pageSize,
                                                                         @RequestParam(required = false) String... sort) {
        log.info("Start get order with sort by multiple columns : ");
        try{
            PageResponse<?> orders = orderService.getAllOrdersByMultipleColumns(pageNo,pageSize,sort)  ;
            return new ApiResponse<>(1000,"get success",orders);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ApiResponse<>(7777,e.getMessage(),null);
        }
    }
    @GetMapping("/getAllOrderWithMultipleColumnsWithSearch")
    public ApiResponse<PageResponse> getAllOrderWithSortByMultipleColumsAndSearch(@RequestParam(defaultValue = "0",required = false) int pageNo,
                                                                                  @RequestParam(defaultValue = "5",required = false) int pageSize,
                                                                                  @RequestParam( required = false) String search,
                                                                                  @RequestParam(required = false) String sortBy) {
        log.info("Start get order with sort by  columns and search : ");
        try{
            PageResponse<?> orders = orderService.getAllOrderWithSortByMultipleColumsAndSearch(pageNo,pageSize,search,sortBy)  ;
            return new ApiResponse<>(1000,"get success",orders);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ApiResponse<>(7777,e.getMessage(),null);
        }
    }
    @GetMapping("/advance-search-by-criteria")
    public ApiResponse<PageResponse> advanceSearchByCriteria(@RequestParam(defaultValue = "0",required = false) int pageNo,
                                                             @RequestParam(defaultValue = "5",required = false) int pageSize,
                                                             @RequestParam( required = false) String sortBy,
                                                             @RequestParam(required = false) String... search) {
        log.info("Start search by criteria : ");
        try{
            PageResponse<?> orders = orderService.advanceSearchByCriteria(pageNo,pageSize,sortBy,search)  ;
            return new ApiResponse<>(1000,"get success",orders);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ApiResponse<>(7777,e.getMessage(),null);
        }
    }
//    @PostMapping("/pay/{tripId}")
//    public  ApiResponse<Order> payOrder(@PathVariable Long tripId){
//        ApiResponse apiResponse = new ApiResponse<>();
//        apiResponse.setData(orderService.payOrderById(tripId));
//        apiResponse.setMessage("pay success");
//        return apiResponse;
//    }

    // @PostMapping("/{orderId}/confirm-payment")
    // public ApiResponse<Order> confirmOrder(@PathVariable Long orderId){
    //     ApiResponse apiResponse = new ApiResponse<>();
    //     log.info("Start confirm payment order : {} ",orderId);
    //     try{
    //         apiResponse.setData(orderService.confirmPayment(orderId));
    //         apiResponse.setMessage("confirm payment success");
    //         return apiResponse;
    //     } catch (Exception e) {
    //         log.error(e.getMessage());
    //         return new ApiResponse<>(7777,e.getMessage(),null);
    //     }
    // }
    @PostMapping("/{orderId}/verifying-payment")
    public ApiResponse<Order> verifyOrder(@PathVariable Long orderId){
        ApiResponse apiResponse = new ApiResponse<>();
        try {
            apiResponse.setData(orderService.verifyPayment(orderId));
            apiResponse.setMessage("verify payment success");
            return apiResponse;
        }catch (Exception e) {
            log.error(e.getMessage());
            return new ApiResponse<>(7777,e.getMessage(),null);
        }
    }
    
    @PostMapping("/{orderId}/confirm-payment")
    public ApiResponse<Order> confirmOrder(@PathVariable Long orderId){
        ApiResponse apiResponse = new ApiResponse<>();
        log.info("Start confirm payment order : {} ",orderId);
        try{
            apiResponse.setData(orderService.confirmPayment(orderId));
            apiResponse.setMessage("confirm payment success");
            return apiResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ApiResponse<>(7777,e.getMessage(),null);
        }
    }
    @PostMapping("/{orderId}/payment-falled")
    public ApiResponse<Order> paymentFalledOrder(@PathVariable Long orderId){
        ApiResponse apiResponse = new ApiResponse<>();
        try {
            apiResponse.setData(orderService.payFalled(orderId));
            apiResponse.setMessage("pay falled ");
            return apiResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ApiResponse<>(7777,e.getMessage(),null);
        }
    }

}