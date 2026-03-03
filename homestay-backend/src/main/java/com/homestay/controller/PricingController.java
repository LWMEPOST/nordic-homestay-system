package com.homestay.controller;

import com.homestay.entity.PricingRule;
import com.homestay.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @GetMapping("/calculate")
    public ResponseEntity<BigDecimal> calculatePrice(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(pricingService.calculatePrice(roomId, date));
    }

    @PostMapping("/rules")
    public ResponseEntity<PricingRule> createRule(@RequestBody PricingRule rule) {
        return ResponseEntity.ok(pricingService.createRule(rule));
    }

    @DeleteMapping("/rules/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        pricingService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rules/room/{roomId}")
    public ResponseEntity<List<PricingRule>> getRulesByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(pricingService.getRulesByRoom(roomId));
    }
}
