package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.Invoice;
import com.sanh.chungcu.entity.Payment;
import com.sanh.chungcu.enums.InvoiceStatus;
import com.sanh.chungcu.enums.PaymentStatus;
import com.sanh.chungcu.repository.InvoiceRepository;
import com.sanh.chungcu.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentRepository repository;
    private final InvoiceRepository invoiceRepository;

    public PaymentController(PaymentRepository repository, InvoiceRepository invoiceRepository) {
        this.repository = repository;
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping
    public List<Payment> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Payment create(@RequestBody Payment item) {
        item.setId(null);
        return repository.save(item);
    }

    /**
     * Nghiệp vụ thanh toán hóa đơn: ghi nhận khoản thanh toán VÀ cập nhật
     * trạng thái hóa đơn sang PAID khi tổng số tiền đã trả đủ.
     * Dùng endpoint này thay cho POST /api/payments thuần CRUD ở phía cư dân.
     */
    @PostMapping("/settle/{invoiceId}")
    @Transactional
    public ResponseEntity<?> settle(@PathVariable Integer invoiceId, @RequestBody Payment item) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElse(null);
        if (invoice == null) {
            return ResponseEntity.notFound().build();
        }
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            return ResponseEntity.badRequest().body(Map.of("message", "Hóa đơn này đã được thanh toán."));
        }

        item.setId(null);
        item.setInvoice(invoice);
        if (item.getAmount() == null) {
            item.setAmount(invoice.getTotalAmount());
        }
        if (item.getStatus() == null) {
            item.setStatus(PaymentStatus.SUCCESS);
        }
        Payment saved = repository.save(item);

        // Cộng dồn các khoản đã thanh toán thành công của hóa đơn này
        BigDecimal paid = repository.findAll().stream()
                .filter(pm -> pm.getInvoice() != null
                        && invoiceId.equals(pm.getInvoice().getId())
                        && pm.getStatus() == PaymentStatus.SUCCESS
                        && pm.getAmount() != null)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = invoice.getTotalAmount() == null ? BigDecimal.ZERO : invoice.getTotalAmount();
        if (paid.compareTo(total) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoiceRepository.save(invoice);
        }

        return ResponseEntity.ok(Map.of(
                "payment", saved,
                "invoiceStatus", invoice.getStatus(),
                "paidAmount", paid,
                "totalAmount", total
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> update(@PathVariable Integer id, @RequestBody Payment item) {
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
