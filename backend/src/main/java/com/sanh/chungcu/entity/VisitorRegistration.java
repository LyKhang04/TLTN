package com.sanh.chungcu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sanh.chungcu.entity.User;
import com.sanh.chungcu.enums.VisitorStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "visitor_registrations")
@Getter
@Setter
public class VisitorRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User resident;

    @Column(name = "guest_name", length = 100)
    private String guestName;

    @Column(name = "guest_phone", length = 15)
    private String guestPhone;

    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Column(name = "expected_time")
    private LocalDateTime expectedTime;

    @Column(name = "qr_code", length = 100)
    private String qrCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private VisitorStatus status;

}
