package com.homestay.controller;

import com.homestay.entity.User;
import com.homestay.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PasswordTestController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @GetMapping("/api/public/encode")
    public String encode(@RequestParam String password) {
        return passwordEncoder.encode(password);
    }
    
    @GetMapping("/api/public/match")
    public boolean match(@RequestParam String password, @RequestParam String encoded) {
        return passwordEncoder.matches(password, encoded);
    }

    @GetMapping("/api/public/reset-admin")
    public String resetAdminPassword() {
        User admin = userRepository.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
        
        String newPassword = passwordEncoder.encode("admin123");
        admin.setPassword(newPassword);
        userRepository.save(admin);
        
        return "Admin password reset to 'admin123'. New hash: " + newPassword;
    }
}
