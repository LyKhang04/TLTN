package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.AccessLog;
import com.sanh.chungcu.entity.ResidentCard;
import com.sanh.chungcu.enums.CardStatus;
import com.sanh.chungcu.enums.LogDirection;
import com.sanh.chungcu.repository.AccessLogRepository;
import com.sanh.chungcu.repository.ResidentCardRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/access-logs")
public class AccessLogController {

    private final AccessLogRepository repository;
    private final ResidentCardRepository cardRepository;

    public AccessLogController(AccessLogRepository repository,
                               ResidentCardRepository cardRepository) {
        this.repository = repository;
        this.cardRepository = cardRepository;
    }

    /**
     * Bảo vệ quét thẻ cư dân tại cửa. Hệ thống tra thẻ theo mã, kiểm tra thẻ
     * còn hiệu lực rồi ghi một dòng nhật ký ra/vào.
     *
     * Body: { "cardCode": "RC-0001", "direction": "IN", "gate": "Cong chinh" }
     */
    @PostMapping("/scan")
    @Transactional
    public ResponseEntity<?> scan(@RequestBody Map<String, Object> body) {
        String cardCode = text(body.get("cardCode"));
        if (cardCode == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng nhập mã thẻ."));
        }

        ResidentCard card = cardRepository.findByCardCodeIgnoreCase(cardCode).orElse(null);
        if (card == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Không tìm thấy thẻ có mã " + cardCode + " trong hệ thống."));
        }
        if (card.getStatus() != null && card.getStatus() != CardStatus.ACTIVE) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Thẻ " + cardCode + " đang ở trạng thái " + card.getStatus()
                            + ", không được phép qua cửa."));
        }

        LogDirection direction;
        try {
            direction = LogDirection.valueOf(text(body.get("direction")).trim().toUpperCase());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Chiều di chuyển không hợp lệ. Chấp nhận: IN, OUT."));
        }

        AccessLog log = new AccessLog();
        log.setCard(card);
        log.setDirection(direction);
        log.setGate(text(body.get("gate")) == null ? "Cong chinh" : text(body.get("gate")));
        log.setLoggedAt(LocalDateTime.now());
        return ResponseEntity.ok(repository.save(log));
    }

    // ===================== CRUD =====================

    @GetMapping
    public List<AccessLog> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccessLog> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AccessLog create(@RequestBody AccessLog item) {
        item.setId(null);
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccessLog> update(@PathVariable Integer id, @RequestBody AccessLog item) {
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

    private static String text(Object o) {
        if (o == null) return null;
        String s = o.toString().trim();
        return s.isEmpty() ? null : s;
    }
}
