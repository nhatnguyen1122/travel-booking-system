package edu.hust.travelbookingsystem.service;

import edu.hust.travelbookingsystem.entity.Hotel;
import edu.hust.travelbookingsystem.model.request.HotelDTO;

import java.util.List;

public interface HotelService {
    public Hotel createHotel(HotelDTO hotelDTO);
    public Hotel getHotel(Long hotelId);
    public List<Hotel> getAllHotels();
    public Hotel updateHotel(HotelDTO hotelDTO,Long hotelId);
    public void deleteHotel(Long hotelId);
    public List<Hotel> getHotelsByDestination(String destination);
}
