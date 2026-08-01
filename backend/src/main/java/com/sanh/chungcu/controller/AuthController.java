package com.sanh.chungcu.controller;

import com.sanh.chungcu.dto.LoginRequest;
import com.sanh.chungcu.dto.UserSummary;
import com.sanh.chungcu.entity.User;
import com.sanh.chungcu.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Đăng nhập bằng username + password.
     * Demo đơn giản: trả về thông tin người dùng nếu khớp mật khẩu.
     * Sản phẩm thật cần phát hành JWT/session token ở đây.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userRepository.findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(request.getUsername()))
                .findFirst()
                .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPasswordHash()))
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(new UserSummary(u)))
                .orElse(ResponseEntity.status(401).body(Map.of("message", "Sai tài khoản hoặc mật khẩu")));
    }

    @GetMapping("/me/{id}")
    public ResponseEntity<UserSummary> me(@PathVariable Integer id) {
        return userRepository.findById(id)
                .map(u -> ResponseEntity.ok(new UserSummary(u)))
                .orElse(ResponseEntity.notFound().build());
    }
}
