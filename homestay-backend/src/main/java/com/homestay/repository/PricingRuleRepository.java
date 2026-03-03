package com.homestay.repository;

import com.homestay.entity.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {
    List<PricingRule> findByRoomId(Long roomId);
    List<PricingRule> findByRoomIdIsNull(); // Global rules
}
