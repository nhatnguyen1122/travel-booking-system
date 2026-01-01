package edu.hust.travelbookingsystem.service.implementation;

import edu.hust.travelbookingsystem.entity.Flight;
import edu.hust.travelbookingsystem.entity.Order;
import edu.hust.travelbookingsystem.enums.ErrorCode;
import edu.hust.travelbookingsystem.exception.AppException;
import edu.hust.travelbookingsystem.model.request.FlightDTO;
import edu.hust.travelbookingsystem.repository.FlightRepository;
import edu.hust.travelbookingsystem.repository.OrderRepository;
import edu.hust.travelbookingsystem.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FlightServiceImplementation implements FlightService {
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Flight createFlight(FlightDTO flightDTO) {
        // check xem ngày check in có sau thời điểm hiện tại không
        if(flightDTO.getCheckInDate().before(new Date())){
            throw new AppException(ErrorCode.DATE_NOT_VALID) ;
        }
        // ngày check in phải trước check out
        if(!flightDTO.getCheckInDate().before(flightDTO.getCheckOutDate())){
            throw new AppException(ErrorCode.DATE_TIME_NOT_VALID);
        }
        Flight flight = new Flight();

        flight.setTicketClass(flightDTO.getTicketClass());
        flight.setAirlineName(flightDTO.getAirlineName());
        flight.setPrice(flightDTO.getPrice());
        flight.setCheckInDate(flightDTO.getCheckInDate());
        flight.setCheckOutDate(flightDTO.getCheckOutDate());
        flight.setNumberOfChairs(flightDTO.getNumberOfChairs());
        flight.setSeatAvailable(flightDTO.getNumberOfChairs());
        return flightRepository.save(flight);
    }

    @Override
    public void deleteFlight(Long id) {

        Flight flight = flightRepository.findById(id).get();
        //trước khi xóa chuyến bay cập nhật ở bảng order trước
        List<Order> orders = orderRepository.findByFlight(flight) ;
        for(Order order : orders){
            order.setFlight(null);
        }
        orderRepository.saveAll(orders);
        flightRepository.delete(flight);
        return ;
    }

    @Override
    public Flight updateFlight(Long id, FlightDTO flightDTO) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTS));
        // check xem ngày check in có sau thời điểm hiện tại không
        if(flightDTO.getCheckInDate().before(new Date())){
            throw new AppException(ErrorCode.DATE_NOT_VALID) ;
        }
        // ngày check in phải trước check out
        if(!flightDTO.getCheckInDate().before(flightDTO.getCheckOutDate())){
            throw new AppException(ErrorCode.DATE_TIME_NOT_VALID);
        }
        // tính lại số ghế thừa

        if(flightDTO.getNumberOfChairs() >= flight.getNumberOfChairs()){
            flight.setSeatAvailable(flight.getSeatAvailable()+flightDTO.getNumberOfChairs()-flight.getNumberOfChairs());
        }else {
            int soGheDaDuocDat = flight.getNumberOfChairs()-flight.getSeatAvailable();
            if(flightDTO.getNumberOfChairs()<soGheDaDuocDat){
                throw new AppException(ErrorCode.NUMBER_CHAIR_NOT_VALID);
            }else{
                flight.setSeatAvailable(flightDTO.getNumberOfChairs()-soGheDaDuocDat);
            }
        }


        flight.setTicketClass(flightDTO.getTicketClass());
        flight.setAirlineName(flightDTO.getAirlineName());
        flight.setPrice(flightDTO.getPrice());
        flight.setCheckInDate(flightDTO.getCheckInDate());
        flight.setCheckOutDate(flightDTO.getCheckOutDate());
        flight.setNumberOfChairs(flightDTO.getNumberOfChairs());
        return flightRepository.save(flight);
    }

    @Override
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }
}
