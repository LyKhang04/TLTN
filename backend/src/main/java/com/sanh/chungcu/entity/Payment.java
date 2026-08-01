package com.sanh.chungcu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sanh.chungcu.entity.Invoice;
import com.sanh.chungcu.entity.User;
import com.sanh.chungcu.enums.PaymentMethod;
import com.sanh.chungcu.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "received_by")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User receivedBy;

    @Column(name = "amount")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", length = 30)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private PaymentStatus status;

}
