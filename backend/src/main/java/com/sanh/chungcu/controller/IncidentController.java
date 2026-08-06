package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.Incident;
import com.sanh.chungcu.entity.IncidentLog;
import com.sanh.chungcu.entity.User;
import com.sanh.chungcu.enums.IncidentStatus;
import com.sanh.chungcu.repository.IncidentLogRepository;
import com.sanh.chungcu.repository.IncidentRepository;
import com.sanh.chungcu.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentRepository repository;
    private final IncidentLogRepository logRepository;
    private final UserRepository userRepository;

    public IncidentController(IncidentRepository repository,
                              IncidentLogRepository logRepository,
                              UserRepository userRepository) {
        this.repository = repository;
        this.logRepository = logRepository;
        this.userRepository = userRepository;
    }

    /**
     * Ban quản lý phân công một nhân viên xử lý sự cố.
     * Sự cố chuyển sang trạng thái IN_PROGRESS và ghi lại nhật ký xử lý.
     *
     * Body: { "assignedToId": 2, "approvedById": 1, "note": "Giao to ky thuat" }
     */
    @PostMapping("/{id}/assign")
    @Transactional
    public ResponseEntity<?> assign(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        Incident incident = repository.findById(id).orElse(null);
        if (incident == null) {
            return ResponseEntity.notFound().build();
        }
        if (incident.getStatus() == IncidentStatus.RESOLVED) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Sự cố đã xử lý xong, không thể phân công lại."));
        }

        User assignedTo = findUser(body.get("assignedToId"));
        if (assignedTo == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Không tìm thấy nhân viên được phân công."));
        }
        User approvedBy = findUser(body.get("approvedById"));

        incident.setAssignedTo(assignedTo);
        if (approvedBy != null) {
            incident.setApprovedBy(approvedBy);
        }
        incident.setStatus(IncidentStatus.IN_PROGRESS);
        repository.save(incident);

        writeLog(incident, approvedBy, IncidentStatus.IN_PROGRESS.name(),
                text(body.get("note"), "Đã phân công cho " + assignedTo.getFullName()));

        return ResponseEntity.ok(incident);
    }

    /**
     * Cập nhật tiến độ hoặc kết thúc xử lý sự cố.
     * Khi chuyển sang RESOLVED, hệ thống tự ghi nhận thời điểm hoàn thành.
     *
     * Body: { "status": "RESOLVED", "updatedById": 2, "note": "Da thay van nuoc" }
     */
    @PostMapping("/{id}/status")
    @Transactional
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        Incident incident = repository.findById(id).orElse(null);
        if (incident == null) {
            return ResponseEntity.notFound().build();
        }

        IncidentStatus newStatus;
        try {
            newStatus = IncidentStatus.valueOf(String.valueOf(body.get("status")).trim().toUpperCase());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Trạng thái không hợp lệ. Chấp nhận: NEW, IN_PROGRESS, RESOLVED, REJECTED."));
        }

        incident.setStatus(newStatus);
        if (newStatus == IncidentStatus.RESOLVED) {
            incident.setResolvedAt(LocalDateTime.now());
        }
        repository.save(incident);

        User updatedBy = findUser(body.get("updatedById"));
        writeLog(incident, updatedBy, newStatus.name(),
                text(body.get("note"), "Cập nhật trạng thái: " + newStatus));

        return ResponseEntity.ok(incident);
    }

    /** Xem nhật ký xử lý của một sự cố, sắp theo thứ tự ghi nhận. */
    @GetMapping("/{id}/logs")
    public List<IncidentLog> logs(@PathVariable Integer id) {
        return logRepository.findAll().stream()
                .filter(l -> l.getIncident() != null && id.equals(l.getIncident().getId()))
                .sorted((a, b) -> Integer.compare(
                        a.getId() == null ? 0 : a.getId(),
                        b.getId() == null ? 0 : b.getId()))
                .toList();
    }

    private void writeLog(Incident incident, User updatedBy, String status, String note) {
        IncidentLog log = new IncidentLog();
        log.setIncident(incident);
        log.setUpdatedBy(updatedBy);
        log.setStatus(status);
        log.setNote(note);
        logRepository.save(log);
    }

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

    private static String text(Object o, String fallback) {
        String s = o == null ? null : o.toString().trim();
        return (s == null || s.isEmpty()) ? fallback : s;
    }

    @GetMapping
    public List<Incident> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Incident create(@RequestBody Incident item) {
        item.setId(null);
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Incident> update(@PathVariable Integer id, @RequestBody Incident item) {
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
}
