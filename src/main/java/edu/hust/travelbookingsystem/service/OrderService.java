package edu.hust.travelbookingsystem.service;

import edu.hust.travelbookingsystem.entity.Order;
import edu.hust.travelbookingsystem.model.request.OrderDTO;
import edu.hust.travelbookingsystem.model.request.OrderHotelDTO;
import edu.hust.travelbookingsystem.model.response.PageResponse;

import java.util.List;

public interface OrderService {
    Order addOrder(OrderDTO orderDTO, Long userId);

    Order chooseHotel(Long orderId, Long HotelId, OrderHotelDTO orderHotelDTO);

    Order saveOrder(Order order);

    Order chooseFlight(Long orderId, Long flightId);

    Order chooseFlightWithSeats(Long orderId, Long flightId, List<String> seatNumbers);

    void cancelOrder(Long orderId);

    Order cancelFlight(Long orderId);

    PageResponse getOrdersByUserId(Long userId, int pageNo, int pageSize);

    PageResponse getAllOrders(int pageNo, int pageSize, String sortBy);

    PageResponse getAllOrdersByMultipleColumns(int pageNo, int pageSize, String... sorts);

    PageResponse getAllOrderWithSortByMultipleColumsAndSearch(int pageNo, int pageSize, String search, String sortBy);

    PageResponse advanceSearchByCriteria(int pageNo, int pageSize, String sortBy, String... search);

    Order confirmPayment(Long orderId);

    Order verifyPayment(Long orderId);

    Order payFalled(Long orderId);
}