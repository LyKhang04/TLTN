package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.User;
import com.sanh.chungcu.entity.VisitorCheckin;
import com.sanh.chungcu.entity.VisitorRegistration;
import com.sanh.chungcu.enums.VisitorStatus;
import com.sanh.chungcu.repository.UserRepository;
import com.sanh.chungcu.repository.VisitorCheckinRepository;
import com.sanh.chungcu.repository.VisitorRegistrationRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visitor-registrations")
public class VisitorRegistrationController {

    private final VisitorRegistrationRepository repository;
    private final VisitorCheckinRepository checkinRepository;
    private final UserRepository userRepository;

    public VisitorRegistrationController(VisitorRegistrationRepository repository,
                                         VisitorCheckinRepository checkinRepository,
                                         UserRepository userRepository) {
        this.repository = repository;
        this.checkinRepository = checkinRepository;
        this.userRepository = userRepository;
    }

    // ===================== Nghiệp vụ dành cho Bảo vệ / Lễ tân =====================

    /**
     * Bảo vệ duyệt một đăng ký khách. Hệ thống sinh mã QR để khách xuất trình
     * tại cửa; mã được tạo từ id đăng ký nên không trùng lặp.
     *
     * Body (tùy chọn): { "guardId": 2 }
     */
    @PostMapping("/{id}/approve")
    @Transactional
    public ResponseEntity<?> approve(@PathVariable Integer id,
                                     @RequestBody(required = false) Map<String, Object> body) {
        VisitorRegistration reg = repository.findById(id).orElse(null);
        if (reg == null) {
            return ResponseEntity.notFound().build();
        }
        if (reg.getStatus() == VisitorStatus.CANCELLED) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Đăng ký đã bị hủy, không thể duyệt."));
        }
        if (reg.getStatus() != null && reg.getStatus() != VisitorStatus.PENDING) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Đăng ký này đã được xử lý trước đó."));
        }

        reg.setStatus(VisitorStatus.APPROVED);
        if (reg.getQrCode() == null || reg.getQrCode().isBlank()) {
            reg.setQrCode(String.format("SANH-KH-%05d", reg.getId()));
        }
        return ResponseEntity.ok(repository.save(reg));
    }

    /** Bảo vệ hủy một đăng ký khách (khách không tới, thông tin sai...). */
    @PostMapping("/{id}/cancel")
    @Transactional
    public ResponseEntity<?> cancel(@PathVariable Integer id) {
        VisitorRegistration reg = repository.findById(id).orElse(null);
        if (reg == null) {
            return ResponseEntity.notFound().build();
        }
        if (reg.getStatus() == VisitorStatus.CHECKED_IN) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Khách đang ở trong tòa nhà, không thể hủy đăng ký."));
        }
        reg.setStatus(VisitorStatus.CANCELLED);
        return ResponseEntity.ok(repository.save(reg));
    }

    /**
     * Ghi nhận khách vào tòa nhà: tạo bản ghi visitor_checkins kèm bảo vệ trực
     * và chuyển đăng ký sang trạng thái CHECKED_IN.
     *
     * Body (tùy chọn): { "guardId": 2 }
     */
    @PostMapping("/{id}/checkin")
    @Transactional
    public ResponseEntity<?> checkin(@PathVariable Integer id,
                                     @RequestBody(required = false) Map<String, Object> body) {
        VisitorRegistration reg = repository.findById(id).orElse(null);
        if (reg == null) {
            return ResponseEntity.notFound().build();
        }
        if (reg.getStatus() != VisitorStatus.APPROVED) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Chỉ đăng ký đã được duyệt mới cho phép khách vào."));
        }

        VisitorCheckin checkin = new VisitorCheckin();
        checkin.setVisitorRegistration(reg);
        checkin.setGuard(findUser(body == null ? null : body.get("guardId")));
        checkin.setCheckinTime(LocalDateTime.now());
        VisitorCheckin savedCheckin = checkinRepository.save(checkin);

        reg.setStatus(VisitorStatus.CHECKED_IN);
        repository.save(reg);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("registration", reg);
        result.put("checkin", savedCheckin);
        return ResponseEntity.ok(result);
    }

    /**
     * Ghi nhận khách rời tòa nhà: cập nhật giờ ra cho lượt vào gần nhất
     * và chuyển đăng ký sang CHECKED_OUT.
     */
    @PostMapping("/{id}/checkout")
    @Transactional
    public ResponseEntity<?> checkout(@PathVariable Integer id) {
        VisitorRegistration reg = repository.findById(id).orElse(null);
        if (reg == null) {
            return ResponseEntity.notFound().build();
        }
        if (reg.getStatus() != VisitorStatus.CHECKED_IN) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Khách chưa được ghi nhận vào tòa nhà."));
        }

        VisitorCheckin open = checkinRepository
                .findFirstByVisitorRegistration_IdAndCheckoutTimeIsNullOrderByIdDesc(id)
                .orElse(null);
        if (open != null) {
            open.setCheckoutTime(LocalDateTime.now());
            checkinRepository.save(open);
        }

        reg.setStatus(VisitorStatus.CHECKED_OUT);
        repository.save(reg);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("registration", reg);
        result.put("checkin", open);
        return ResponseEntity.ok(result);
    }

    /** Lịch sử vào/ra của một đăng ký khách. */
    @GetMapping("/{id}/checkins")
    public List<VisitorCheckin> checkins(@PathVariable Integer id) {
        return checkinRepository.findByVisitorRegistration_IdOrderByIdAsc(id);
    }

    // ===================== CRUD =====================

    @GetMapping
    public List<VisitorRegistration> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitorRegistration> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public VisitorRegistration create(@RequestBody VisitorRegistration item) {
        item.setId(null);
        if (item.getStatus() == null) {
            item.setStatus(VisitorStatus.PENDING);
        }
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VisitorRegistration> update(@PathVariable Integer id, @RequestBody VisitorRegistration item) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        item.setId(id);
        return ResponseEntity.ok(repository.save(item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Tiện ích nội bộ ---

    private User findUser(Object rawId) {
        if (rawId == null) {
            return null;
        }
        try {
            Integer uid = rawId instanceof Number n ? n.intValue() : Integer.valueOf(rawId.toString().trim());
            return userRepository.findById(uid).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
