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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OrderServiceImplementation implements OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private FlightRepository flightRepository;
    @Autowired private SearchRepository searchRepository;
    @Autowired private HotelBedroomRepository hotelBedroomRepository;
    @Autowired private HotelBookingRepository hotelBookingRepository;
    @Autowired private PayRepository payRepository;
    @Autowired private FlightSeatServiceImplementation flightSeatService;

    // ---------------------------
    // LocalDate helpers
    // ---------------------------
    private static LocalDate toLocalDate(Date d) {
        if (d == null) return null;
        if (d instanceof java.sql.Date sd) return sd.toLocalDate();
        if (d instanceof Timestamp ts) return ts.toLocalDateTime().toLocalDate();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static void requireNonNull(LocalDate d, ErrorCode code) {
        if (d == null) throw new AppException(code);
    }

    private static boolean inRangeInclusive(LocalDate v, LocalDate start, LocalDate end) {
        return v != null && !v.isBefore(start) && !v.isAfter(end);
    }

    /** value ∈ [base, base+1] */
    private static void assertForwardMax1Day(LocalDate value, LocalDate base, ErrorCode code) {
        if (!inRangeInclusive(value, base, base.plusDays(1))) throw new AppException(code);
    }

    /** base ∈ [value, value+1]  (dùng cho: tourEnd ∈ [fr, fr+1]) */
    private static void assertBaseWithinNext1Day(LocalDate value, LocalDate base, ErrorCode code) {
        if (!inRangeInclusive(base, value, value.plusDays(1))) throw new AppException(code);
    }

    // -------------------------------------------------
    // Core business
    // -------------------------------------------------
    @Override
    @Transactional
    public Order addOrder(OrderDTO orderDTO, Long userId) {
        if (!userRepository.existsById(userId)) throw new AppException(ErrorCode.USER_NOT_EXISTS);

        if (orderDTO.getCheckInDate().before(new Date())) throw new AppException(ErrorCode.DATE_NOT_VALID);

        if (!orderDTO.getCheckInDate().before(orderDTO.getCheckOutDate()))
            throw new AppException(ErrorCode.DATE_TIME_NOT_VALID);

        Order order = new Order();
        order.setDestination(orderDTO.getDestination());
        order.setNumberOfPeople(orderDTO.getNumberOfPeople());
        order.setCheckinDate(orderDTO.getCheckInDate());
        order.setCheckoutDate(orderDTO.getCheckOutDate());
        order.setUser(userRepository.findById(userId).get());

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order chooseHotel(Long orderId, Long hotelId, OrderHotelDTO orderHotelDTO) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        LocalDate tourStart = toLocalDate(order.getCheckinDate());
        LocalDate tourEnd   = toLocalDate(order.getCheckoutDate());
        requireNonNull(tourStart, ErrorCode.DATE_TIME_NOT_VALID);
        requireNonNull(tourEnd, ErrorCode.DATE_TIME_NOT_VALID);

        LocalDate hotelIn  = toLocalDate(orderHotelDTO.getStartHotel());
        LocalDate hotelOut = toLocalDate(orderHotelDTO.getEndHotel());
        requireNonNull(hotelIn, ErrorCode.DATE_TIME_NOT_VALID);
        requireNonNull(hotelOut, ErrorCode.DATE_TIME_NOT_VALID);

        // basic ranges (existing behavior)
        if (!hotelIn.isBefore(hotelOut)) throw new AppException(ErrorCode.HOTEL_END_DATE_INVALID);
        if (hotelIn.isBefore(tourStart)) throw new AppException(ErrorCode.DATE_INVALID);
        if (hotelOut.isAfter(tourEnd))   throw new AppException(ErrorCode.HOTEL_DATE_EXCEEDS_CHECKOUT);

        // ✅ STRICT PROXIMITY FEASIBILITY (KHÔNG query flight)
        // Inbound: hotelIn <= tourStart + 2
        if (hotelIn.isAfter(tourStart.plusDays(2))) {
            throw new AppException(ErrorCode.HOTEL_CHECKIN_TOO_FAR_FROM_TOUR_START);
        }
        // Outbound: hotelOut >= tourEnd - 2
        if (hotelOut.isBefore(tourEnd.minusDays(2))) {
            throw new AppException(ErrorCode.HOTEL_CHECKOUT_TOO_FAR_FROM_TOUR_END);
        }

        // If already selected a flight -> validate strict chain fully
        Flight existingFlight = order.getFlight();
        if (existingFlight != null) {
            LocalDate fd = toLocalDate(existingFlight.getCheckInDate());
            LocalDate fr = toLocalDate(existingFlight.getCheckOutDate());
            requireNonNull(fd, ErrorCode.NOT_VALID_FLIGHT_DATE);
            requireNonNull(fr, ErrorCode.NOT_VALID_FLIGHT_DATE);

            // TourStart vs FlightDepart: fd ∈ [tourStart, tourStart+1]
            assertForwardMax1Day(fd, tourStart, ErrorCode.NOT_VALID_FLIGHT_DATE);

            // FlightDepart vs HotelCheckin: hotelIn ∈ [fd, fd+1]
            assertForwardMax1Day(hotelIn, fd, ErrorCode.DATE_INVALID);

            // HotelCheckout vs FlightReturn: fr ∈ [hotelOut, hotelOut+1]
            if (!inRangeInclusive(fr, hotelOut, hotelOut.plusDays(1))) {
                throw new AppException(ErrorCode.NOT_VALID_FLIGHT_DATE);
            }

            // FlightReturn vs TourEnd: tourEnd ∈ [fr, fr+1]
            assertBaseWithinNext1Day(fr, tourEnd, ErrorCode.FLIGHT_DATE_EXCEEDS_CHECKOUT);
        }

        // set into order
        order.setHotel(hotel);
        order.setStartHotel(orderHotelDTO.getStartHotel());
        order.setEndHotel(orderHotelDTO.getEndHotel());

        // bookings + price
        StringBuilder bedroomsStr = new StringBuilder();
        double totalPrice = 0;

        for (HotelBedroom hbRequest : orderHotelDTO.getHotelBedroomList()) {
            Long bedroomId = hbRequest.getId();
            if (bedroomId == null) continue;

            HotelBedroom bedroom = hotelBedroomRepository.findById(bedroomId)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTS));

            if (bedroom.getHotel() == null || bedroom.getHotel().getId() == null
                    || !bedroom.getHotel().getId().equals(hotelId)) {
                throw new AppException(ErrorCode.NOT_EXISTS);
            }

            List<HotelBooking> overlaps = hotelBookingRepository.findOverLappingBookings(
                    hotelId, bedroomId, orderHotelDTO.getStartHotel(), orderHotelDTO.getEndHotel()
            );
            if (!overlaps.isEmpty()) throw new AppException(ErrorCode.HOTEL_BEDROOM_NOT_AVAILABLE);

            HotelBooking booking = new HotelBooking();
            booking.setHotel(hotel);
            booking.setHotelBedroom(bedroom);
            booking.setOrder(order);
            booking.setStartDate(orderHotelDTO.getStartHotel());
            booking.setEndDate(orderHotelDTO.getEndHotel());
            hotelBookingRepository.save(booking);

            bedroomsStr.append(bedroom.getRoomNumber()).append(" ");
            totalPrice += bedroom.getPrice();
        }

        order.setListBedrooms(bedroomsStr.toString().trim());
        order.setTotalPrice(order.getTotalPrice() + totalPrice);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order chooseFlight(Long orderId, Long flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTS));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        LocalDate tourStart = toLocalDate(order.getCheckinDate());
        LocalDate tourEnd   = toLocalDate(order.getCheckoutDate());
        requireNonNull(tourStart, ErrorCode.DATE_TIME_NOT_VALID);
        requireNonNull(tourEnd, ErrorCode.DATE_TIME_NOT_VALID);

        LocalDate fd = toLocalDate(flight.getCheckInDate());
        LocalDate fr = toLocalDate(flight.getCheckOutDate());
        requireNonNull(fd, ErrorCode.NOT_VALID_FLIGHT_DATE);
        requireNonNull(fr, ErrorCode.NOT_VALID_FLIGHT_DATE);

        // ✅ Strict constraints (Arrival)
        assertForwardMax1Day(fd, tourStart, ErrorCode.NOT_VALID_FLIGHT_DATE);

        // ✅ Strict constraints (Return): tourEnd ∈ [fr, fr+1]
        assertBaseWithinNext1Day(fr, tourEnd, ErrorCode.FLIGHT_DATE_EXCEEDS_CHECKOUT);

        // If hotel already selected, validate strict chain with hotel
        LocalDate hotelIn  = toLocalDate(order.getStartHotel());
        LocalDate hotelOut = toLocalDate(order.getEndHotel());

        if (hotelIn != null && hotelOut != null) {
            // hotelIn ∈ [fd, fd+1]
            assertForwardMax1Day(hotelIn, fd, ErrorCode.DATE_INVALID);

            // fr ∈ [hotelOut, hotelOut+1]
            if (!inRangeInclusive(fr, hotelOut, hotelOut.plusDays(1))) {
                throw new AppException(ErrorCode.NOT_VALID_FLIGHT_DATE);
            }
        }

        // seats
        if (flight.getSeatAvailable() < order.getNumberOfPeople()) {
            throw new AppException(ErrorCode.NOT_ENOUGH_SEATS);
        }

        flight.setSeatAvailable(flight.getSeatAvailable() - order.getNumberOfPeople());
        order.setFlight(flight);

        order.setTotalPrice(order.getTotalPrice() + order.getNumberOfPeople() * flight.getPrice());
        order.setPayment(payRepository.findByStatus(PaymentStatus.UNPAID)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_UNPAID_NOT_EXISTS)));

        flightRepository.save(flight);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order chooseFlightWithSeats(Long orderId, Long flightId, List<String> seatNumbers) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTS));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        LocalDate tourStart = toLocalDate(order.getCheckinDate());
        LocalDate tourEnd   = toLocalDate(order.getCheckoutDate());
        requireNonNull(tourStart, ErrorCode.DATE_TIME_NOT_VALID);
        requireNonNull(tourEnd, ErrorCode.DATE_TIME_NOT_VALID);

        LocalDate fd = toLocalDate(flight.getCheckInDate());
        LocalDate fr = toLocalDate(flight.getCheckOutDate());
        requireNonNull(fd, ErrorCode.NOT_VALID_FLIGHT_DATE);
        requireNonNull(fr, ErrorCode.NOT_VALID_FLIGHT_DATE);

        // strict
        assertForwardMax1Day(fd, tourStart, ErrorCode.NOT_VALID_FLIGHT_DATE);
        assertBaseWithinNext1Day(fr, tourEnd, ErrorCode.FLIGHT_DATE_EXCEEDS_CHECKOUT);

        LocalDate hotelIn  = toLocalDate(order.getStartHotel());
        LocalDate hotelOut = toLocalDate(order.getEndHotel());
        if (hotelIn != null && hotelOut != null) {
            assertForwardMax1Day(hotelIn, fd, ErrorCode.DATE_INVALID);
            if (!inRangeInclusive(fr, hotelOut, hotelOut.plusDays(1))) {
                throw new AppException(ErrorCode.NOT_VALID_FLIGHT_DATE);
            }
        }

        if (seatNumbers.size() != order.getNumberOfPeople()) {
            throw new AppException(ErrorCode.SEAT_COUNT_MISMATCH);
        }

        boolean seatsBooked = flightSeatService.bookSeats(flight, order, seatNumbers);
        if (!seatsBooked) {
            throw new AppException(ErrorCode.SEATS_NOT_AVAILABLE);
        }

        if (flight.getSeatAvailable() < order.getNumberOfPeople()) {
            throw new AppException(ErrorCode.NOT_ENOUGH_SEATS);
        }

        flight.setSeatAvailable(flight.getSeatAvailable() - order.getNumberOfPeople());
        order.setFlight(flight);

        order.setTotalPrice(order.getTotalPrice() + order.getNumberOfPeople() * flight.getPrice());
        order.setPayment(payRepository.findByStatus(PaymentStatus.UNPAID)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_UNPAID_NOT_EXISTS)));

        flightRepository.save(flight);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        hotelBookingRepository.deleteByOrderId(orderId);

        Flight flight = order.getFlight();
        if (flight != null) {
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
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        Flight flight = order.getFlight();
        if (flight != null) {
            flightSeatService.releaseSeats(orderId);
            flight.setSeatAvailable(flight.getSeatAvailable() + order.getNumberOfPeople());
            order.setTotalPrice(order.getTotalPrice() - order.getNumberOfPeople() * flight.getPrice());
            flightRepository.save(flight);
        }
        order.setFlight(null);
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse getOrdersByUserId(Long userId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        Page<Order> orders = orderRepository.findByUser(user, pageable);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(orders.getTotalPages())
                .items(orders.getContent())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse getAllOrders(int pageNo, int pageSize, String sortBy) {
        List<Sort.Order> sorts = new ArrayList<>();
        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                sorts.add(new Sort.Order(
                        matcher.group(3).equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                        matcher.group(1)
                ));
            }
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sorts));
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
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                ordersSort.add(new Sort.Order(
                        matcher.group(3).equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                        matcher.group(1)
                ));
            }
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(ordersSort));
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
        return searchRepository.getAllOrderWithSortByMultipleColumsAndSearch(pageNo, pageSize, search, sortBy);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse advanceSearchByCriteria(int pageNo, int pageSize, String sortBy, String... search) {
        return searchRepository.advanceSearchOrder(pageNo, pageSize, sortBy, search);
    }

    @Override
    @Transactional
    public Order confirmPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        Payment payment = payRepository.findByStatus(PaymentStatus.PAID)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_PAID_NOT_EXISTS));
        order.setPayment(payment);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order verifyPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setPayment(payRepository.findByStatus(PaymentStatus.VERIFYING)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_VERIFY_NOT_EXISTS)));
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order payFalled(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setPayment(payRepository.findByStatus(PaymentStatus.PAYMENT_FAILED)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_FALSE_NOT_EXISTS)));
        return orderRepository.save(order);
    }
}
