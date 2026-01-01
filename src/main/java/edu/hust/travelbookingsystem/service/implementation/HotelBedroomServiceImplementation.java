package edu.hust.travelbookingsystem.service.implementation;

import edu.hust.travelbookingsystem.entity.Hotel;
import edu.hust.travelbookingsystem.entity.HotelBedroom;
import edu.hust.travelbookingsystem.enums.ErrorCode;
import edu.hust.travelbookingsystem.exception.AppException;
import edu.hust.travelbookingsystem.model.request.HotelBedroomDTO;
import edu.hust.travelbookingsystem.repository.HotelBedroomRepository;
import edu.hust.travelbookingsystem.repository.HotelRepository;
import edu.hust.travelbookingsystem.service.HotelBedroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HotelBedroomServiceImplementation implements HotelBedroomService {

    @Autowired
    private HotelBedroomRepository hotelBedroomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Override
    @Transactional
    public HotelBedroom createRoom(HotelBedroomDTO dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

        if (hotelBedroomRepository.existsByHotelIdAndRoomNumber(dto.getHotelId(), dto.getRoomNumber())) {
            throw new AppException(ErrorCode.ROOM_NUMBER_EXISTS);
        }

        if (dto.getPrice() < 0) {
            throw new AppException(ErrorCode.PRICE_NOT_VALID);
        }

        HotelBedroom room = new HotelBedroom();
        room.setRoomNumber(dto.getRoomNumber());
        room.setPrice(dto.getPrice());
        room.setRoomType(dto.getRoomType());
        room.setHotel(hotel);

        return hotelBedroomRepository.save(room);
    }

    @Override
    @Transactional
    public HotelBedroom updateRoom(Long roomId, HotelBedroomDTO dto) {
        HotelBedroom room = hotelBedroomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        // Check if room number already exists for another room in the same hotel
        if (hotelBedroomRepository.existsByHotelIdAndRoomNumberAndIdNot(
                room.getHotel().getId(), dto.getRoomNumber(), roomId)) {
            throw new AppException(ErrorCode.ROOM_NUMBER_EXISTS);
        }

        if (dto.getPrice() < 0) {
            throw new AppException(ErrorCode.PRICE_NOT_VALID);
        }

        room.setRoomNumber(dto.getRoomNumber());
        room.setPrice(dto.getPrice());
        room.setRoomType(dto.getRoomType());

        return hotelBedroomRepository.save(room);
    }

    @Override
    @Transactional
    public void deleteRoom(Long roomId) {
        HotelBedroom room = hotelBedroomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        hotelBedroomRepository.delete(room);
    }

    @Override
    public List<HotelBedroom> getRoomsByHotel(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new AppException(ErrorCode.HOTEL_NOT_FOUND);
        }
        return hotelBedroomRepository.findByHotelId(hotelId);
    }

    @Override
    public HotelBedroom getRoom(Long roomId) {
        return hotelBedroomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
    }
}
