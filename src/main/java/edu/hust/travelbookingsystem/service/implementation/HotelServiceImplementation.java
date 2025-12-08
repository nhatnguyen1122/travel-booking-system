package edu.hust.travelbookingsystem.service.implementation;

import edu.hust.travelbookingsystem.convert.HotelConverter;
import edu.hust.travelbookingsystem.entity.Hotel;
import edu.hust.travelbookingsystem.enums.ErrorCode;
import edu.hust.travelbookingsystem.exception.AppException;
import edu.hust.travelbookingsystem.model.request.HotelDTO;
import edu.hust.travelbookingsystem.repository.HotelRepository;
import edu.hust.travelbookingsystem.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelServiceImplementation implements HotelService {
    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private HotelConverter hotelConverter;



    @Override
    public Hotel createHotel(HotelDTO hotelDTO) {
        Hotel hotel = hotelConverter.convertHotel(hotelDTO);
        if(hotelDTO.getPriceFrom() <0){
            throw  new AppException(ErrorCode.PRICE_NOT_VALID);
        }
        return hotelRepository.save(hotel);
    }

    @Override
    public Hotel getHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
        return hotel;
    }

    @Override
    public List<Hotel> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels;
    }

    @Override
    public Hotel updateHotel(HotelDTO hotelDTO , Long hotelId){
        if(hotelDTO.getPriceFrom() <0){
            throw  new AppException(ErrorCode.PRICE_NOT_VALID);
        }
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
        hotel.setHotelName(hotelDTO.getHotelName());
        hotel.setHotelPriceFrom(hotelDTO.getPriceFrom());
        hotel.setAddress(hotelDTO.getAddress());
        return hotelRepository.save(hotel);
    }

    @Override
    public void deleteHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

        hotelRepository.deleteById(hotelId);
    }

    @Override
    public List<Hotel> getHotelsByDestination(String destination) {
        return hotelRepository.findByDestination(destination);
    }
}
