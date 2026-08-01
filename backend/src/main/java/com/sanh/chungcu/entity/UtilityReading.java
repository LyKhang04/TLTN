package com.sanh.chungcu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sanh.chungcu.entity.Apartment;
import com.sanh.chungcu.enums.UtilityType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "utility_readings")
@Getter
@Setter
public class UtilityReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Apartment apartment;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30)
    private UtilityType type;

    @Column(name = "reading_value")
    private BigDecimal readingValue;

    @Column(name = "reading_date")
    private LocalDate readingDate;

}
