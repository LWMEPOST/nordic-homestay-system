package com.homestay.repository;

import com.homestay.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {
    List<RoomAvailability> findByRoomIdAndDateBetween(Long roomId, LocalDate startDate, LocalDate endDate);
}
