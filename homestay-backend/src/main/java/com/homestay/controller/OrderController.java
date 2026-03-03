package com.homestay.controller;

import com.homestay.dto.OrderRequest;
import com.homestay.entity.Order;
import com.homestay.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.homestay.dto.OrderDTO;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request, Authentication authentication) {
        return ResponseEntity.ok(orderService.createOrder(authentication.getName(), request));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Order> payOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.payOrder(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    @PostMapping("/{id}/check-in")
    public ResponseEntity<Order> checkIn(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.checkIn(id));
    }

    @PostMapping("/{id}/check-out")
    public ResponseEntity<Order> checkOut(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.checkOut(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderDTO>> getUserOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getUserOrders(authentication.getName()));
    }
    
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
