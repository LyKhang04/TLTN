package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.VisitorCheckin;
import com.sanh.chungcu.repository.VisitorCheckinRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitor-checkins")
public class VisitorCheckinController {

    private final VisitorCheckinRepository repository;

    public VisitorCheckinController(VisitorCheckinRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<VisitorCheckin> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitorCheckin> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public VisitorCheckin create(@RequestBody VisitorCheckin item) {
        item.setId(null);
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VisitorCheckin> update(@PathVariable Integer id, @RequestBody VisitorCheckin item) {
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
