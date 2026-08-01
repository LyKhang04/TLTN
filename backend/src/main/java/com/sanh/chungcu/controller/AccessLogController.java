package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.AccessLog;
import com.sanh.chungcu.repository.AccessLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/access-logs")
public class AccessLogController {

    private final AccessLogRepository repository;

    public AccessLogController(AccessLogRepository repository) {
        this.repository = repository;
    }

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
}
