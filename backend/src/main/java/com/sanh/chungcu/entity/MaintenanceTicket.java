package com.sanh.chungcu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sanh.chungcu.enums.MaintenanceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Phieu bao tri thiet bi / hang muc trong chung cu.
 *
 * Phieu co the phat sinh tu mot su co do cu dan bao (incident_id),
 * hoac do Ban quan ly chu dong len ke hoach bao tri dinh ky (incident_id = null).
 */
@Entity
@Table(name = "maintenance_tickets")
@Getter
@Setter
public class MaintenanceTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Su co goc (neu phieu bao tri sinh ra tu phan anh cua cu dan). */
    @ManyToOne
    @JoinColumn(name = "incident_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Incident incident;

    /** Can ho lien quan; de trong neu bao tri khu vuc chung. */
    @ManyToOne
    @JoinColumn(name = "apartment_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Apartment apartment;

    /** Nhan vien ky thuat duoc phan cong thuc hien. */
    @ManyToOne
    @JoinColumn(name = "assigned_to")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User assignedTo;

    /** Nguoi tao phieu (nhan su Ban quan ly). */
    @ManyToOne
    @JoinColumn(name = "created_by")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User createdBy;

    @Column(name = "title", length = 150)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Hang muc: Thang may, He thong dien, PCCC, May bom nuoc... */
    @Column(name = "category", length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private MaintenanceStatus status;

    /** Ngay du kien thuc hien. */
    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /** Chi phi bao tri thuc te. */
    @Column(name = "cost")
    private BigDecimal cost;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
