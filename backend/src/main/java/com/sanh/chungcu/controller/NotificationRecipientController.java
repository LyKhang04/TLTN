package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.NotificationRecipient;
import com.sanh.chungcu.repository.NotificationRecipientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification-recipients")
public class NotificationRecipientController {

    private final NotificationRecipientRepository repository;

    public NotificationRecipientController(NotificationRecipientRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<NotificationRecipient> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationRecipient> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public NotificationRecipient create(@RequestBody NotificationRecipient item) {
        item.setId(null);
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationRecipient> update(@PathVariable Integer id, @RequestBody NotificationRecipient item) {
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
