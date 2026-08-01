package com.sanh.chungcu.controller;

import com.sanh.chungcu.enums.ApartmentStatus;
import com.sanh.chungcu.enums.IncidentStatus;
import com.sanh.chungcu.enums.InvoiceStatus;
import com.sanh.chungcu.repository.ApartmentRepository;
import com.sanh.chungcu.repository.IncidentRepository;
import com.sanh.chungcu.repository.InvoiceRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ApartmentRepository apartmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final IncidentRepository incidentRepository;

    public DashboardController(ApartmentRepository apartmentRepository,
                                InvoiceRepository invoiceRepository,
                                IncidentRepository incidentRepository) {
        this.apartmentRepository = apartmentRepository;
        this.invoiceRepository = invoiceRepository;
        this.incidentRepository = incidentRepository;
    }

    /** Số liệu tổng quan cho trang Dashboard của Ban quản lý. */
    @GetMapping("/summary")
    public Map<String, Object> summary() {
        long totalApartments = apartmentRepository.count();
        long occupied = apartmentRepository.findAll().stream()
                .filter(a -> a.getStatus() == ApartmentStatus.OCCUPIED)
                .count();
        long unpaidInvoices = invoiceRepository.findAll().stream()
                .filter(i -> i.getStatus() == InvoiceStatus.UNPAID || i.getStatus() == InvoiceStatus.OVERDUE)
                .count();
        long openIncidents = incidentRepository.findAll().stream()
                .filter(i -> i.getStatus() == IncidentStatus.NEW || i.getStatus() == IncidentStatus.IN_PROGRESS)
                .count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalApartments", totalApartments);
        result.put("occupiedApartments", occupied);
        result.put("unpaidInvoices", unpaidInvoices);
        result.put("openIncidents", openIncidents);
        return result;
    }
}
