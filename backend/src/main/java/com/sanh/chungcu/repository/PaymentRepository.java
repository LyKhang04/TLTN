package com.sanh.chungcu.repository;

import com.sanh.chungcu.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
