package com.sanh.chungcu.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "service_price_configs")
@Getter
@Setter
public class ServicePriceConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "service_name", length = 100)
    private String serviceName;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

}
