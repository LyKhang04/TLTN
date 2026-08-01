package com.sanh.chungcu.repository;

import com.sanh.chungcu.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    List<Invoice> findByApartment_Id(Integer apartmentId);
}
