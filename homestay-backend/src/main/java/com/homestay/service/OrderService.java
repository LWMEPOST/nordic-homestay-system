package com.homestay.service;

import com.homestay.dto.OrderRequest;
import com.homestay.entity.Order;
import com.homestay.entity.Room;
import com.homestay.entity.RoomAvailability;
import com.homestay.entity.User;
import com.homestay.repository.OrderRepository;
import com.homestay.repository.RoomAvailabilityRepository;
import com.homestay.repository.RoomRepository;
import com.homestay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.homestay.entity.PricingRule;
import com.homestay.repository.PricingRuleRepository;

import com.homestay.dto.OrderDTO;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final PricingService pricingService;

    @Transactional
    public Order createOrder(String username, OrderRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Check availability
        if (!isRoomAvailable(request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate())) {
            throw new RuntimeException("Room is not available for selected dates");
        }

        // Calculate total price
        BigDecimal totalAmount = BigDecimal.ZERO;
        LocalDate currentDate = request.getCheckInDate();
        while (currentDate.isBefore(request.getCheckOutDate())) {
            totalAmount = totalAmount.add(pricingService.calculatePrice(room.getId(), currentDate));
            currentDate = currentDate.plusDays(1);
        }

        // Create order
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString());
        order.setUserId(user.getId());
        order.setRoomId(room.getId());
        order.setCheckInDate(request.getCheckInDate());
        order.setCheckOutDate(request.getCheckOutDate());
        order.setGuestCount(request.getGuestCount());
        order.setGuestName(request.getGuestName());
        order.setGuestPhone(request.getGuestPhone());
        order.setTotalAmount(totalAmount);
        order.setStatus(0); // 待支付
        order.setRemark(request.getRemark());
        
        Order savedOrder = orderRepository.save(order);

        // Lock room availability
        lockRoomAvailability(savedOrder);

        return savedOrder;
    }

    private boolean isRoomAvailable(Long roomId, LocalDate startDate, LocalDate endDate) {
        List<RoomAvailability> availabilities = roomAvailabilityRepository.findByRoomIdAndDateBetween(roomId, startDate, endDate.minusDays(1));
        return availabilities.isEmpty();
    }

    private void lockRoomAvailability(Order order) {
        LocalDate currentDate = order.getCheckInDate();
        while (currentDate.isBefore(order.getCheckOutDate())) {
            RoomAvailability availability = new RoomAvailability();
            availability.setRoomId(order.getRoomId());
            availability.setDate(currentDate);
            availability.setStatus(1); // 已预订
            availability.setOrderId(order.getId());
            roomAvailabilityRepository.save(availability);
            currentDate = currentDate.plusDays(1);
        }
    }

    @Transactional
    public Order payOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() != 0) {
            throw new RuntimeException("Order status is not pending payment");
        }

        order.setStatus(1); // 已支付
        order.setPayTime(LocalDateTime.now());
        return orderRepository.save(order);
    }
    
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Release room availability
        List<RoomAvailability> availabilities = roomAvailabilityRepository.findByRoomIdAndDateBetween(
                order.getRoomId(), order.getCheckInDate(), order.getCheckOutDate().minusDays(1));
        roomAvailabilityRepository.deleteAll(availabilities);

        order.setStatus(4); // 已取消
        return orderRepository.save(order);
    }

    @Transactional
    public Order checkIn(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() != 1) { // Must be PAID
            throw new RuntimeException("Order must be paid before check-in");
        }

        order.setStatus(2); // 已入住
        return orderRepository.save(order);
    }

    @Transactional
    public Order checkOut(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() != 2) { // Must be CHECKED_IN
            throw new RuntimeException("Order must be checked-in before check-out");
        }

        order.setStatus(3); // 已完成
        
        // Release room availability (optional, or keep history)
        // Usually we keep availability record as 'occupied' for history
        
        return orderRepository.save(order);
    }

    public List<OrderDTO> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Order> orders = orderRepository.findByUserId(user.getId());
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setUserId(order.getUserId());
        dto.setRoomId(order.getRoomId());
        dto.setCheckInDate(order.getCheckInDate());
        dto.setCheckOutDate(order.getCheckOutDate());
        dto.setGuestCount(order.getGuestCount());
        dto.setGuestName(order.getGuestName());
        dto.setGuestPhone(order.getGuestPhone());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setRemark(order.getRemark());
        dto.setCreateTime(order.getCreateTime());
        dto.setPayTime(order.getPayTime());

        // Enrich with Room Info
        roomRepository.findById(order.getRoomId()).ifPresent(room -> {
            dto.setRoomName(room.getName());
            if (room.getImages() != null && !room.getImages().isEmpty()) {
                dto.setRoomImage(room.getImages().get(0));
            }
        });
        
        // Enrich with User Info
        userRepository.findById(order.getUserId()).ifPresent(user -> {
            dto.setUsername(user.getUsername());
        });

        return dto;
    }
}
