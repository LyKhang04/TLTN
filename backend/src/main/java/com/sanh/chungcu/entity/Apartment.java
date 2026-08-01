package com.sanh.chungcu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sanh.chungcu.entity.Building;
import com.sanh.chungcu.enums.ApartmentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "apartments")
@Getter
@Setter
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "building_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Building building;

    @Column(name = "code", length = 20)
    private String code;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "area")
    private BigDecimal area;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private ApartmentStatus status;

}
