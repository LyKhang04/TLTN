package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.VisitorRegistration;
import com.sanh.chungcu.repository.VisitorRegistrationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitor-registrations")
public class VisitorRegistrationController {

    private final VisitorRegistrationRepository repository;

    public VisitorRegistrationController(VisitorRegistrationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<VisitorRegistration> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitorRegistration> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public VisitorRegistration create(@RequestBody VisitorRegistration item) {
        item.setId(null);
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VisitorRegistration> update(@PathVariable Integer id, @RequestBody VisitorRegistration item) {
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
