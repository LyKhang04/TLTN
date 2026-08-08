package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.User;
import com.sanh.chungcu.entity.Vehicle;
import com.sanh.chungcu.entity.VehicleLog;
import com.sanh.chungcu.enums.LogDirection;
import com.sanh.chungcu.enums.VehicleStatus;
import com.sanh.chungcu.repository.UserRepository;
import com.sanh.chungcu.repository.VehicleLogRepository;
import com.sanh.chungcu.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicle-logs")
public class VehicleLogController {

    private final VehicleLogRepository repository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public VehicleLogController(VehicleLogRepository repository,
                                VehicleRepository vehicleRepository,
                                UserRepository userRepository) {
        this.repository = repository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    /**
     * Bảo vệ ghi nhận xe ra/vào hầm. Hệ thống tra phương tiện theo biển số
     * đã đăng ký, cảnh báo nếu xe không thuộc căn hộ nào trong tòa nhà.
     *
     * Body: { "plateNumber": "59A1-123.45", "direction": "IN",
     *         "gate": "Ham B1", "guardId": 2 }
     */
    @PostMapping("/scan")
    @Transactional
    public ResponseEntity<?> scan(@RequestBody Map<String, Object> body) {
        String plate = text(body.get("plateNumber"));
        if (plate == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng nhập biển số xe."));
        }

        Vehicle vehicle = vehicleRepository.findByPlateNumberIgnoreCase(plate).orElse(null);
        if (vehicle == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "message", "Biển số " + plate + " chưa được đăng ký. "
                            + "Đề nghị liên hệ Ban quản lý trước khi cho xe vào."));
        }
        if (vehicle.getStatus() != null && vehicle.getStatus() != VehicleStatus.ACTIVE) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Phương tiện " + plate + " đang ngưng hoạt động."));
        }

        LogDirection direction;
        try {
            direction = LogDirection.valueOf(text(body.get("direction")).trim().toUpperCase());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Chiều di chuyển không hợp lệ. Chấp nhận: IN, OUT."));
        }

        VehicleLog log = new VehicleLog();
        log.setVehicle(vehicle);
        log.setGuard(findUser(body.get("guardId")));
        log.setDirection(direction);
        log.setGate(text(body.get("gate")) == null ? "Ham B1" : text(body.get("gate")));
        log.setLoggedAt(LocalDateTime.now());
        return ResponseEntity.ok(repository.save(log));
    }

    // ===================== CRUD =====================

    @GetMapping
    public List<VehicleLog> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleLog> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public VehicleLog create(@RequestBody VehicleLog item) {
        item.setId(null);
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleLog> update(@PathVariable Integer id, @RequestBody VehicleLog item) {
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

    private static String text(Object o) {
        if (o == null) return null;
        String s = o.toString().trim();
        return s.isEmpty() ? null : s;
    }
}
