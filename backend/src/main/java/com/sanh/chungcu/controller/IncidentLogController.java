package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.IncidentLog;
import com.sanh.chungcu.repository.IncidentLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incident-logs")
public class IncidentLogController {

    private final IncidentLogRepository repository;

    public IncidentLogController(IncidentLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<IncidentLog> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentLog> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public IncidentLog create(@RequestBody IncidentLog item) {
        item.setId(null);
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidentLog> update(@PathVariable Integer id, @RequestBody IncidentLog item) {
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
