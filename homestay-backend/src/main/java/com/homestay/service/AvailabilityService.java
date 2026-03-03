package com.homestay.service;

import com.homestay.entity.RoomAvailability;
import com.homestay.repository.RoomAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final RoomAvailabilityRepository roomAvailabilityRepository;

    public List<RoomAvailability> getAvailability(Long roomId, LocalDate startDate, LocalDate endDate) {
        return roomAvailabilityRepository.findByRoomIdAndDateBetween(roomId, startDate, endDate);
    }
}
