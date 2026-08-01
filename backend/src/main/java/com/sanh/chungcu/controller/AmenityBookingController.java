package com.sanh.chungcu.controller;

import com.sanh.chungcu.entity.AmenityBooking;
import com.sanh.chungcu.repository.AmenityBookingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/amenity-bookings")
public class AmenityBookingController {

    private final AmenityBookingRepository repository;

    public AmenityBookingController(AmenityBookingRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<AmenityBooking> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AmenityBooking> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AmenityBooking create(@RequestBody AmenityBooking item) {
        item.setId(null);
        return repository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AmenityBooking> update(@PathVariable Integer id, @RequestBody AmenityBooking item) {
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
