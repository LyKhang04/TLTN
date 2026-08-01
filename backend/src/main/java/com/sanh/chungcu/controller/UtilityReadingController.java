package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.UtilityReading;
import com.sanh.chungcu.repository.UtilityReadingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utility-readings")
public class UtilityReadingController {

    private final UtilityReadingRepository repository;

    public UtilityReadingController(UtilityReadingRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<UtilityReading> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilityReading> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public UtilityReading create(@RequestBody UtilityReading item) {
        item.setId(null);
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UtilityReading> update(@PathVariable Integer id, @RequestBody UtilityReading item) {
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
