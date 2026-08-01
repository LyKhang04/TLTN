package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.PaymentTransaction;
import com.sanh.chungcu.repository.PaymentTransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-transactions")
public class PaymentTransactionController {

    private final PaymentTransactionRepository repository;

    public PaymentTransactionController(PaymentTransactionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<PaymentTransaction> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentTransaction> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public PaymentTransaction create(@RequestBody PaymentTransaction item) {
        item.setId(null);
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentTransaction> update(@PathVariable Integer id, @RequestBody PaymentTransaction item) {
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
