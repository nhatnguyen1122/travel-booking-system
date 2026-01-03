package edu.hust.travelbookingsystem.service.implementation;

import edu.hust.travelbookingsystem.entity.*;
import edu.hust.travelbookingsystem.enums.ErrorCode;
import edu.hust.travelbookingsystem.enums.PaymentStatus;
import edu.hust.travelbookingsystem.exception.AppException;
import edu.hust.travelbookingsystem.model.request.OrderDTO;
import edu.hust.travelbookingsystem.model.request.OrderHotelDTO;
import edu.hust.travelbookingsystem.model.response.PageResponse;
import edu.hust.travelbookingsystem.repository.*;
import edu.hust.travelbookingsystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OrderServiceImplementation implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private SearchRepository searchRepository;
    @Autowired
    private HotelBedroomRepository hotelBedroomRepository;
    @Autowired
    private HotelBookingRepository hotelBookingRepository;
    @Autowired
    private PayRepository payRepository;
    @Autowired
    private FlightSeatServiceImplementation flightSeatService;

    @Override
    @Transactional
    public Order addOrder(OrderDTO orderDTO, Long userId) {
        Order order = new Order();
        if(!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTS) ;
        }
        // Check-in date must be in the future
        if(orderDTO.getCheckInDate().before(new Date())){
            throw new AppException(ErrorCode.DATE_NOT_VALID);
        }
        // Check-in date must be before check-out date
        if(!orderDTO.getCheckInDate().before(orderDTO.getCheckOutDate())){
            throw new AppException(ErrorCode.DATE_TIME_NOT_VALID);
        }
        order.setDestination(orderDTO.getDestination());
        order.setNumberOfPeople(orderDTO.getNumberOfPeople());
        order.setCheckinDate(orderDTO.getCheckInDate());
        order.setCheckoutDate(orderDTO.getCheckOutDate());

        User user = userRepository.findById(userId).get();
        order.setUser(user);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order chooseHotel(Long orderId, Long hotelId, OrderHotelDTO orderHotelDTO) {
        // Tìm Hotel theo hotelId
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

        // Tìm Order theo orderId
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Hotel start date must be on or after order check-in date
        if(orderHotelDTO.getStartHotel().before(order.getCheckinDate())){
            throw new AppException(ErrorCode.DATE_INVALID);
        }

        // Hotel end date must be after hotel start date
        if(!orderHotelDTO.getStartHotel().before(orderHotelDTO.getEndHotel())){
            throw new AppException(ErrorCode.HOTEL_END_DATE_INVALID);
        }

        // Hotel end date must not exceed order check-out date
        if(orderHotelDTO.getEndHotel().after(order.getCheckoutDate())){
            throw new AppException(ErrorCode.HOTEL_DATE_EXCEEDS_CHECKOUT);
        }

        order.setHotel(hotel);
        order.setStartHotel(orderHotelDTO.getStartHotel());
        order.setEndHotel(orderHotelDTO.getEndHotel());

        String listBedrooms = "" ;
        double totalPrice = 0 ;
        for(HotelBedroom hotelBedroom : orderHotelDTO.getHotelBedroomList()){
            // kiem tra co bi chong cheo lich khong
            List<HotelBooking> hotelBookings = hotelBookingRepository.findOverLappingBookings(hotelId ,hotelBedroom.getId(),orderHotelDTO.getStartHotel(),orderHotelDTO.getEndHotel());
            if(!hotelBookings.isEmpty()){
                throw new AppException(ErrorCode.HOTEL_BEDROOM_NOT_AVAILABLE) ;
            }
            HotelBooking  hotelBooking = new HotelBooking();
            hotelBooking.setHotel(hotel);
            hotelBooking.setHotelBedroom(hotelBedroom);
            hotelBooking.setOrder(order);
            hotelBooking.setStartDate(orderHotelDTO.getStartHotel());
            hotelBooking.setEndDate(orderHotelDTO.getEndHotel());

            // save data
            hotelBookingRepository.save(hotelBooking);
            listBedrooms += hotelBedroom.getRoomNumber() + " " ;
            totalPrice += hotelBedroom.getPrice();
        }
        order.setListBedrooms(listBedrooms);
        // tính tiền
        order.setTotalPrice(order.getTotalPrice()+totalPrice);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order chooseFlight(Long orderId , Long flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(()->new AppException(ErrorCode.NOT_EXISTS)) ;

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Flight date must be on or after order check-in date
        if(flight.getCheckInDate().before(order.getCheckinDate())){
            throw new AppException(ErrorCode.NOT_VALID_FLIGHT_DATE) ;
        }

        // Flight date must not exceed order check-out date
        if(flight.getCheckInDate().after(order.getCheckoutDate())){
            throw new AppException(ErrorCode.FLIGHT_DATE_EXCEEDS_CHECKOUT);
        }

        // Check if enough seats are available
        if(flight.getSeatAvailable() < order.getNumberOfPeople()){
            throw new AppException(ErrorCode.NOT_ENOUGH_SEATS);
        }

        flight.setSeatAvailable(flight.getSeatAvailable()-order.getNumberOfPeople());// cập nhật số ghees thừa
        order.setFlight(flight);
        // tính tiền
        order.setTotalPrice(order.getTotalPrice()+order.getNumberOfPeople()*flight.getPrice());
        //xác nhận tình trạng thanh toán
        order.setPayment(payRepository.findByStatus(PaymentStatus.UNPAID).orElseThrow(()->new AppException(ErrorCode.PAYMENT_UNPAID_NOT_EXISTS)));
        flightRepository.save(flight);
        orderRepository.save(order);
        return order ;
    }

    @Override
    @Transactional
    public Order chooseFlightWithSeats(Long orderId, Long flightId, List<String> seatNumbers) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTS));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Flight date must be on or after order check-in date
        if (flight.getCheckInDate().before(order.getCheckinDate())) {
            throw new AppException(ErrorCode.NOT_VALID_FLIGHT_DATE);
        }

        // Flight date must not exceed order check-out date
        if (flight.getCheckInDate().after(order.getCheckoutDate())) {
            throw new AppException(ErrorCode.FLIGHT_DATE_EXCEEDS_CHECKOUT);
        }

        // Validate seat count matches number of people
        if (seatNumbers.size() != order.getNumberOfPeople()) {
            throw new AppException(ErrorCode.SEAT_COUNT_MISMATCH);
        }

        // Attempt to book seats
        boolean seatsBooked = flightSeatService.bookSeats(flight, order, seatNumbers);
        if (!seatsBooked) {
            throw new AppException(ErrorCode.SEATS_NOT_AVAILABLE);
        }

        // Update flight available seats count
        flight.setSeatAvailable(flight.getSeatAvailable() - order.getNumberOfPeople());
        order.setFlight(flight);

        // Calculate total price
        order.setTotalPrice(order.getTotalPrice() + order.getNumberOfPeople() * flight.getPrice());

        // Set payment status
        order.setPayment(payRepository.findByStatus(PaymentStatus.UNPAID)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_UNPAID_NOT_EXISTS)));

        flightRepository.save(flight);
        orderRepository.save(order);
        return order;
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_FOUND));
        hotelBookingRepository.deleteByOrderId(orderId);
        // Restore flight seats only if a flight was booked
        Flight flight = order.getFlight();
        if(flight != null) {
            // Release individual seats
            flightSeatService.releaseSeats(orderId);
            flight.setSeatAvailable(flight.getSeatAvailable() + order.getNumberOfPeople());
            flightRepository.save(flight);
        }
        orderRepository.delete(order);
    }

    @Override
    @Transactional
    public Order cancelFlight(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_FOUND));
        Flight flight = order.getFlight();
        if(flight != null) {
            // Release individual seats
            flightSeatService.releaseSeats(orderId);
            // Restore seats and subtract flight cost from total
            flight.setSeatAvailable(flight.getSeatAvailable() + order.getNumberOfPeople());
            order.setTotalPrice(order.getTotalPrice() - order.getNumberOfPeople() * flight.getPrice());
            flightRepository.save(flight);
        }
        order.setFlight(null);
        orderRepository.save(order);
        return order ;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse getOrdersByUserId(Long userId , int pageNo , int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        Page<Order> orders = orderRepository.findByUser(user,pageable) ;

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(orders.getTotalPages())
                .items(orders.getContent())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse getAllOrders(int pageNo , int pageSize,String sortBy) {
        List<Sort.Order> sorts = new ArrayList<>();
        // xu ly sort by
        if(StringUtils.hasLength(sortBy)) {
            // orderDate:asc|desc
            Pattern  pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if(matcher.find()) {
                if(matcher.group(3).equalsIgnoreCase("asc")){
                    sorts.add(new Sort.Order(Sort.Direction.ASC,matcher.group(1)));
                }else {
                    sorts.add(new Sort.Order(Sort.Direction.DESC,matcher.group(1)));
                }
            }

        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sorts)); // phan trang co sap xep
        Page<Order> orders = orderRepository.findAll(pageable);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(orders.getTotalPages())
                .items(orders.getContent())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse getAllOrdersByMultipleColumns(int pageNo, int pageSize, String... sorts) {
        List<Sort.Order> ordersSort = new ArrayList<>();
        for (String sortBy : sorts) {
            // orderDate:asc|desc
            Pattern  pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if(matcher.find()) {
                if(matcher.group(3).equalsIgnoreCase("asc")){
                    ordersSort.add(new Sort.Order(Sort.Direction.ASC,matcher.group(1)));
                }else {
                    ordersSort.add(new Sort.Order(Sort.Direction.DESC,matcher.group(1)));
                }
            }
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(ordersSort)); // phan trang co sap xep
        Page<Order> orders = orderRepository.findAll(pageable);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(orders.getTotalPages())
                .items(orders.getContent())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse getAllOrderWithSortByMultipleColumsAndSearch(int pageNo, int pageSize, String search, String sortBy) {
        return searchRepository.getAllOrderWithSortByMultipleColumsAndSearch(pageNo,pageSize,search,sortBy);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse advanceSearchByCriteria(int pageNo, int pageSize, String sortBy, String... search) {
        return searchRepository.advanceSearchOrder(pageNo,pageSize,sortBy,search);
    }

    @Override
    @Transactional
    public Order confirmPayment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_FOUND));
        Payment payment = payRepository.findByStatus(PaymentStatus.PAID).orElseThrow(()-> new AppException(ErrorCode.PAYMENT_PAID_NOT_EXISTS)) ;
        order.setPayment(payment);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order verifyPayment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setPayment(payRepository.findByStatus(PaymentStatus.VERIFYING).orElseThrow(()->new AppException(ErrorCode.PAYMENT_VERIFY_NOT_EXISTS)));
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order payFalled(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setPayment(payRepository.findByStatus(PaymentStatus.PAYMENT_FAILED).orElseThrow(()->new AppException(ErrorCode.PAYMENT_FALSE_NOT_EXISTS)));
        return orderRepository.save(order);
    }
}
