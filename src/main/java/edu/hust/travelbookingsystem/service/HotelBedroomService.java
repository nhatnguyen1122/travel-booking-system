package edu.hust.travelbookingsystem.service;

import edu.hust.travelbookingsystem.entity.HotelBedroom;
import edu.hust.travelbookingsystem.model.request.HotelBedroomDTO;

import java.util.List;

public interface HotelBedroomService {

    HotelBedroom createRoom(HotelBedroomDTO dto);

    HotelBedroom updateRoom(Long roomId, HotelBedroomDTO dto);

    void deleteRoom(Long roomId);

    List<HotelBedroom> getRoomsByHotel(Long hotelId);

    HotelBedroom getRoom(Long roomId);
}
