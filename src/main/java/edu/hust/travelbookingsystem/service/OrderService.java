package edu.hust.travelbookingsystem.service;

import edu.hust.travelbookingsystem.entity.Order;
import edu.hust.travelbookingsystem.model.request.OrderDTO;
import edu.hust.travelbookingsystem.model.request.OrderHotelDTO;
import edu.hust.travelbookingsystem.model.response.PageResponse;

public interface OrderService {
    public Order addOrder(OrderDTO orderDTO, Long userId);
    public Order chooseHotel(Long orderId, Long HotelId, OrderHotelDTO orderHotelDTO);
    public  Order saveOrder(Order order) ;
    public Order chooseFlight(Long orderId , Long flightId);
    public void cancelOrder(Long orderId);
    public Order cancelFlight(Long orderId) ;
    public PageResponse getOrdersByUserId(Long userId,int pageNo, int pageSize);
    public PageResponse getAllOrders(int pageNo, int pageSize,String sortBy);
    public PageResponse getAllOrdersByMultipleColumns(int pageNo, int pageSize,String... sorts); // String... == List<String>
    public PageResponse getAllOrderWithSortByMultipleColumsAndSearch(int pageNo, int pageSize,String search,String sortBy);
    public PageResponse advanceSearchByCriteria(int pageNo, int pageSize,String sortBy,String... search);
//    public Order payOrderById(Long orderId);

    public Order confirmPayment(Long orderId);
    public Order verifyPayment(Long orderId);
    public Order payFalled(Long orderId);
}
