package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.Incident;
import com.sanh.chungcu.entity.MaintenanceTicket;
import com.sanh.chungcu.entity.User;
import com.sanh.chungcu.enums.MaintenanceStatus;
import com.sanh.chungcu.repository.IncidentRepository;
import com.sanh.chungcu.repository.MaintenanceTicketRepository;
import com.sanh.chungcu.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance-tickets")
public class MaintenanceTicketController {

    private final MaintenanceTicketRepository repository;
    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;

    public MaintenanceTicketController(MaintenanceTicketRepository repository,
                                       IncidentRepository incidentRepository,
                                       UserRepository userRepository) {
        this.repository = repository;
        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<MaintenanceTicket> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceTicket> get(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public MaintenanceTicket create(@RequestBody MaintenanceTicket item) {
        item.setId(null);
        if (item.getStatus() == null) {
            item.setStatus(MaintenanceStatus.PENDING);
        }
        if (item.getCreatedAt() == null) {
            item.setCreatedAt(LocalDateTime.now());
        }
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceTicket> update(@PathVariable Integer id,
                                                    @RequestBody MaintenanceTicket item) {
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

    /**
     * Tạo phiếu bảo trì từ một sự cố do cư dân báo.
     * Phiếu tự lấy căn hộ và mô tả từ sự cố gốc, giúp Ban quản lý
     * không phải nhập lại thông tin.
     *
     * Body: { "createdById": 1, "category": "Thang may", "scheduledDate": "2026-08-20" }
     */
    @PostMapping("/from-incident/{incidentId}")
    @Transactional
    public ResponseEntity<?> createFromIncident(@PathVariable Integer incidentId,
                                                @RequestBody(required = false) Map<String, Object> body) {
        Incident incident = incidentRepository.findById(incidentId).orElse(null);
        if (incident == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> b = body == null ? Map.of() : body;

        MaintenanceTicket ticket = new MaintenanceTicket();
        ticket.setIncident(incident);
        ticket.setApartment(incident.getApartment());
        ticket.setTitle(text(b.get("title"), "Bảo trì theo phản ánh #" + incidentId));
        ticket.setDescription(text(b.get("description"), incident.getDescription()));
        ticket.setCategory(text(b.get("category"), incident.getCategory()));
        ticket.setStatus(MaintenanceStatus.PENDING);
        ticket.setScheduledDate(parseDate(b.get("scheduledDate")));
        ticket.setCreatedBy(findUser(b.get("createdById")));
        ticket.setCreatedAt(LocalDateTime.now());

        return ResponseEntity.ok(repository.save(ticket));
    }

    /**
     * Phân công nhân viên kỹ thuật thực hiện phiếu bảo trì.
     * Body: { "assignedToId": 2, "scheduledDate": "2026-08-20" }
     */
    @PostMapping("/{id}/assign")
    @Transactional
    public ResponseEntity<?> assign(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        MaintenanceTicket ticket = repository.findById(id).orElse(null);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        if (ticket.getStatus() == MaintenanceStatus.DONE) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Phiếu đã hoàn thành, không thể phân công lại."));
        }

        User assignedTo = findUser(body.get("assignedToId"));
        if (assignedTo == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Không tìm thấy nhân viên được phân công."));
        }

        ticket.setAssignedTo(assignedTo);
        ticket.setStatus(MaintenanceStatus.IN_PROGRESS);
        LocalDate scheduled = parseDate(body.get("scheduledDate"));
        if (scheduled != null) {
            ticket.setScheduledDate(scheduled);
        }
        return ResponseEntity.ok(repository.save(ticket));
    }

    /**
     * Hoàn thành phiếu bảo trì, ghi nhận chi phí thực tế.
     * Body: { "cost": 1500000 }
     */
    @PostMapping("/{id}/complete")
    @Transactional
    public ResponseEntity<?> complete(@PathVariable Integer id,
                                      @RequestBody(required = false) Map<String, Object> body) {
        MaintenanceTicket ticket = repository.findById(id).orElse(null);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        if (ticket.getStatus() == MaintenanceStatus.CANCELLED) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Phiếu đã bị hủy, không thể hoàn thành."));
        }

        Map<String, Object> b = body == null ? Map.of() : body;
        BigDecimal cost = parseMoney(b.get("cost"));
        if (cost != null) {
            ticket.setCost(cost);
        }
        ticket.setStatus(MaintenanceStatus.DONE);
        ticket.setCompletedAt(LocalDateTime.now());
        return ResponseEntity.ok(repository.save(ticket));
    }

    /**
     * Thống kê chi phí bảo trì: tổng chi phí và số phiếu theo từng trạng thái.
     * Dùng cho phần báo cáo của Ban quản lý.
     */
    @GetMapping("/summary")
    public Map<String, Object> summary() {
        List<MaintenanceTicket> all = repository.findAll();

        BigDecimal totalCost = all.stream()
                .filter(t -> t.getStatus() == MaintenanceStatus.DONE && t.getCost() != null)
                .map(MaintenanceTicket::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (MaintenanceStatus st : MaintenanceStatus.values()) {
            byStatus.put(st.name(), all.stream().filter(t -> t.getStatus() == st).count());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalTickets", all.size());
        result.put("byStatus", byStatus);
        result.put("totalCost", totalCost);
        return result;
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

    private static LocalDate parseDate(Object o) {
        if (o == null) {
            return null;
        }
        try {
            return LocalDate.parse(o.toString().trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static BigDecimal parseMoney(Object o) {
        if (o == null) {
            return null;
        }
        try {
            return new BigDecimal(o.toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String text(Object o, String fallback) {
        String s = o == null ? null : o.toString().trim();
        return (s == null || s.isEmpty()) ? fallback : s;
    }
}
