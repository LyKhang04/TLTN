package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.Invoice;
import com.sanh.chungcu.repository.InvoiceRepository;
import com.sanh.chungcu.service.InvoiceGenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceRepository repository;
    private final InvoiceGenerationService generationService;

    public InvoiceController(InvoiceRepository repository,
                             InvoiceGenerationService generationService) {
        this.repository = repository;
        this.generationService = generationService;
    }

    /**
     * Tự động phát hành hóa đơn dịch vụ cho toàn bộ căn hộ đang có người ở
     * trong một kỳ (tháng/năm). Hệ thống tự tính phí quản lý theo diện tích,
     * tiền điện/nước theo chỉ số đã ghi và phí gửi xe theo số phương tiện.
     *
     * Ví dụ: POST /api/invoices/generate?month=8&year=2026&issuedBy=1
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestParam int month,
                                      @RequestParam int year,
                                      @RequestParam(required = false) Integer issuedBy) {
        try {
            return ResponseEntity.ok(generationService.generateForPeriod(month, year, issuedBy));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    public List<Invoice> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Invoice create(@RequestBody Invoice item) {
        item.setId(null);
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> update(@PathVariable Integer id, @RequestBody Invoice item) {
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
