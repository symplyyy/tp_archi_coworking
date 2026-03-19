package com.hotel.room_service.service;

import com.hotel.room_service.exception.ResourceNotFoundException;
import com.hotel.room_service.kafka.RoomEventProducer;
import com.hotel.room_service.model.Room;
import com.hotel.room_service.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomEventProducer roomEventProducer;

    public RoomService(RoomRepository roomRepository, RoomEventProducer roomEventProducer) {
        this.roomRepository = roomRepository;
        this.roomEventProducer = roomEventProducer;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle introuvable : " + id));
    }

    public Room createRoom(Room room) {
        room.setAvailable(true);
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room updatedRoom) {
        Room room = getRoomById(id);
        room.setName(updatedRoom.getName());
        room.setCity(updatedRoom.getCity());
        room.setCapacity(updatedRoom.getCapacity());
        room.setType(updatedRoom.getType());
        room.setHourlyRate(updatedRoom.getHourlyRate());
        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        Room room = getRoomById(id);
        roomRepository.delete(room);
        roomEventProducer.publishRoomDeleted(id);
    }

    public Room updateAvailability(Long id, boolean available) {
        Room room = getRoomById(id);
        room.setAvailable(available);
        return roomRepository.save(room);
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByAvailable(true);
    }

    public List<Room> getRoomsByCity(String city) {
        return roomRepository.findByCity(city);
    }
}
