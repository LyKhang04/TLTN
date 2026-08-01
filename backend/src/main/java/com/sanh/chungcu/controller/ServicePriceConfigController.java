package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.ServicePriceConfig;
import com.sanh.chungcu.repository.ServicePriceConfigRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-price-configs")
public class ServicePriceConfigController {

    private final ServicePriceConfigRepository repository;

    public ServicePriceConfigController(ServicePriceConfigRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ServicePriceConfig> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicePriceConfig> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ServicePriceConfig create(@RequestBody ServicePriceConfig item) {
        item.setId(null);
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicePriceConfig> update(@PathVariable Integer id, @RequestBody ServicePriceConfig item) {
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
