package com.homestay.service;

import com.homestay.entity.PricingRule;
import com.homestay.entity.Room;
import com.homestay.repository.PricingRuleRepository;
import com.homestay.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final PricingRuleRepository pricingRuleRepository;
    private final RoomRepository roomRepository;

    public BigDecimal calculatePrice(Long roomId, LocalDate date) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        List<PricingRule> rules = new ArrayList<>(pricingRuleRepository.findByRoomId(roomId));
        rules.addAll(pricingRuleRepository.findByRoomIdIsNull()); // Add global rules

        // Filter applicable rules
        List<PricingRule> applicableRules = rules.stream()
                .filter(rule -> isRuleApplicable(rule, date))
                .sorted(Comparator.comparingInt(PricingRule::getPriority).reversed())
                .collect(Collectors.toList());

        BigDecimal finalPrice = room.getBasePrice();

        if (!applicableRules.isEmpty()) {
            // Apply the highest priority rule
            PricingRule rule = applicableRules.get(0);
            if (rule.getPriceMultiplier() != null) {
                finalPrice = finalPrice.multiply(rule.getPriceMultiplier());
            }
            if (rule.getPriceAdd() != null) {
                finalPrice = finalPrice.add(rule.getPriceAdd());
            }
        }

        return finalPrice;
    }

    public List<PricingRule> getRulesByRoom(Long roomId) {
        return pricingRuleRepository.findByRoomId(roomId);
    }

    private boolean isRuleApplicable(PricingRule rule, LocalDate date) {
        switch (rule.getRuleType()) {
            case "CUSTOM":
                return date.equals(rule.getTargetDate());
            case "WEEKEND":
                DayOfWeek day = date.getDayOfWeek();
                return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
            case "HOLIDAY":
                // In a real app, we would check against a holiday calendar
                // For MVP, we might need a Holiday table or hardcoded list
                return false; // Placeholder
            default:
                return false;
        }
    }
    
    public PricingRule createRule(PricingRule rule) {
        return pricingRuleRepository.save(rule);
    }
    
    public void deleteRule(Long id) {
        pricingRuleRepository.deleteById(id);
    }
}
