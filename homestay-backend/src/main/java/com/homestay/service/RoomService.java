package com.homestay.service;

import com.homestay.entity.Room;
import com.homestay.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import com.homestay.entity.User;
import com.homestay.repository.UserRepository;

import com.homestay.dto.RoomDetailDTO;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
    
    public List<Room> getMyRooms(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return roomRepository.findByHostId(user.getId());
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
    }
    
    public RoomDetailDTO getRoomDetail(Long id) {
        Room room = getRoomById(id);
        RoomDetailDTO dto = new RoomDetailDTO();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setDescription(room.getDescription());
        dto.setBasePrice(room.getBasePrice());
        dto.setCapacity(room.getCapacity());
        dto.setImages(room.getImages());
        dto.setFacilities(room.getFacilities());
        dto.setAddress(room.getAddress());
        dto.setCity(room.getCity());
        dto.setStatus(room.getStatus());
        dto.setHostId(room.getHostId());

        if (room.getHostId() != null) {
            userRepository.findById(room.getHostId()).ifPresent(host -> {
                dto.setHostName(host.getNickname() != null ? host.getNickname() : host.getUsername());
                dto.setHostAvatar(host.getAvatar());
                dto.setHostPhone(host.getPhone());
            });
        }
        return dto;
    }

    public Room createRoom(Room room, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        room.setHostId(user.getId());
        // Default status if not set
        if (room.getStatus() == null) {
            room.setStatus(1);
        }
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room roomDetails) {
        Room room = getRoomById(id);
        room.setName(roomDetails.getName());
        room.setDescription(roomDetails.getDescription());
        room.setBasePrice(roomDetails.getBasePrice());
        room.setCapacity(roomDetails.getCapacity());
        room.setImages(roomDetails.getImages());
        room.setFacilities(roomDetails.getFacilities());
        room.setAddress(roomDetails.getAddress());
        room.setCity(roomDetails.getCity());
        room.setStatus(roomDetails.getStatus());
        // Don't update hostId
        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        Room room = getRoomById(id);
        roomRepository.delete(room);
    }
}
