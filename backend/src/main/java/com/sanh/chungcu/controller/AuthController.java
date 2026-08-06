package com.sanh.chungcu.controller;

import com.sanh.chungcu.dto.LoginRequest;
import com.sanh.chungcu.dto.UserSummary;
import com.sanh.chungcu.entity.Role;
import com.sanh.chungcu.entity.User;
import com.sanh.chungcu.enums.UserStatus;
import com.sanh.chungcu.repository.RoleRepository;
import com.sanh.chungcu.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Đăng nhập bằng username + password.
     * Demo đơn giản: trả về thông tin người dùng nếu khớp mật khẩu.
     * Sản phẩm thật cần phát hành JWT/session token ở đây.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> found = findByUsername(request.getUsername());

        if (found.isPresent() && found.get().getStatus() == UserStatus.LOCKED) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Tài khoản đã bị khóa. Vui lòng liên hệ Ban quản lý."));
        }

        return found
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

    /**
     * Đăng ký tài khoản cư dân mới.
     * Tài khoản tạo ra luôn mang vai trò RESIDENT — không cho phép người dùng
     * tự chọn vai trò để tránh leo thang đặc quyền. Việc gán cư dân vào căn hộ
     * do Ban quản lý thực hiện sau ở màn hình quản trị.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        String username = str(body.get("username"));
        String password = str(body.get("password"));
        String fullName = str(body.get("fullName"));
        String email = str(body.get("email"));
        String phone = str(body.get("phone"));

        if (isBlank(username) || isBlank(password) || isBlank(fullName)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Vui lòng nhập đủ tên đăng nhập, mật khẩu và họ tên."));
        }
        if (username.length() < 4) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Tên đăng nhập phải có ít nhất 4 ký tự."));
        }
        if (password.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Mật khẩu phải có ít nhất 6 ký tự."));
        }
        if (findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác."));
        }
        if (!isBlank(email) && userRepository.findAll().stream()
                .anyMatch(u -> email.equalsIgnoreCase(u.getEmail()))) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email này đã được sử dụng."));
        }

        Role residentRole = roleRepository.findAll().stream()
                .filter(r -> "RESIDENT".equalsIgnoreCase(r.getName()))
                .findFirst()
                .orElse(null);
        if (residentRole == null) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Hệ thống chưa cấu hình vai trò RESIDENT."));
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setEmail(isBlank(email) ? null : email);
        user.setPhone(isBlank(phone) ? null : phone);
        user.setRole(residentRole);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        return ResponseEntity.ok(new UserSummary(saved));
    }

    /**
     * Đổi mật khẩu: bắt buộc xác thực lại bằng mật khẩu hiện tại
     * để tránh việc người khác dùng máy đang đăng nhập rồi đổi mật khẩu.
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, Object> body) {
        Integer userId = parseId(body.get("userId"));
        String currentPassword = str(body.get("currentPassword"));
        String newPassword = str(body.get("newPassword"));

        if (userId == null || isBlank(currentPassword) || isBlank(newPassword)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Thiếu thông tin đổi mật khẩu."));
        }
        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Mật khẩu mới phải có ít nhất 6 ký tự."));
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy tài khoản."));
        }
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("message", "Mật khẩu hiện tại không đúng."));
        }
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Mật khẩu mới phải khác mật khẩu cũ."));
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công."));
    }

    // --- Tiện ích nội bộ ---

    private Optional<User> findByUsername(String username) {
        if (isBlank(username)) {
            return Optional.empty();
        }
        return userRepository.findAll().stream()
                .filter(u -> username.trim().equalsIgnoreCase(u.getUsername()))
                .findFirst();
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** Đọc giá trị từ JSON về String, chấp nhận cả khi client gửi kiểu số. */
    private static String str(Object o) {
        return o == null ? null : o.toString().trim();
    }

    /** Đọc id, chấp nhận cả dạng số lẫn chuỗi trong JSON. */
    private static Integer parseId(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Number n) {
            return n.intValue();
        }
        try {
            return Integer.valueOf(o.toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
