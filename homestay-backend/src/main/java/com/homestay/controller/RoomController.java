package com.homestay.controller;

import com.homestay.entity.Room;
import com.homestay.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.homestay.dto.RoomDetailDTO;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }
    
    @GetMapping("/my")
    public List<Room> getMyRooms(Authentication authentication) {
        return roomService.getMyRooms(authentication.getName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDetailDTO> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomDetail(id));
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room, Authentication authentication) {
        return ResponseEntity.ok(roomService.createRoom(room, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room room) {
        return ResponseEntity.ok(roomService.updateRoom(id, room));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
