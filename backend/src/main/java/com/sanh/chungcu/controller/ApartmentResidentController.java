package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.ApartmentResident;
import com.sanh.chungcu.repository.ApartmentResidentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apartment-residents")
public class ApartmentResidentController {

    private final ApartmentResidentRepository repository;

    public ApartmentResidentController(ApartmentResidentRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ApartmentResident> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApartmentResident> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ApartmentResident create(@RequestBody ApartmentResident item) {
        item.setId(null);
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApartmentResident> update(@PathVariable Integer id, @RequestBody ApartmentResident item) {
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
