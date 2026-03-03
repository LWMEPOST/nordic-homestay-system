package com.homestay.repository;

import com.homestay.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByStatus(Integer status);
    List<Room> findByHostId(Long hostId);

    // Find rooms that are available between startDate and endDate
    // This is a complex query that needs to check RoomAvailability
    // For simplicity, we might handle this in service layer or use a custom query
    // checking NOT EXISTS in RoomAvailability
}
