package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.ResidentCard;
import com.sanh.chungcu.repository.ResidentCardRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resident-cards")
public class ResidentCardController {

    private final ResidentCardRepository repository;

    public ResidentCardController(ResidentCardRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ResidentCard> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResidentCard> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResidentCard create(@RequestBody ResidentCard item) {
        item.setId(null);
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResidentCard> update(@PathVariable Integer id, @RequestBody ResidentCard item) {
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
