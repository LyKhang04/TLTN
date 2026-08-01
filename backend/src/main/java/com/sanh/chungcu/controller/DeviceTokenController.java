package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.DeviceToken;
import com.sanh.chungcu.repository.DeviceTokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device-tokens")
public class DeviceTokenController {

    private final DeviceTokenRepository repository;

    public DeviceTokenController(DeviceTokenRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<DeviceToken> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceToken> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public DeviceToken create(@RequestBody DeviceToken item) {
        item.setId(null);
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceToken> update(@PathVariable Integer id, @RequestBody DeviceToken item) {
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
