package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.VehicleLog;
import com.sanh.chungcu.repository.VehicleLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle-logs")
public class VehicleLogController {

    private final VehicleLogRepository repository;

    public VehicleLogController(VehicleLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<VehicleLog> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleLog> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public VehicleLog create(@RequestBody VehicleLog item) {
        item.setId(null);
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleLog> update(@PathVariable Integer id, @RequestBody VehicleLog item) {
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
