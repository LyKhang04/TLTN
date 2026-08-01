package com.sanh.chungcu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sanh.chungcu.entity.Apartment;
import com.sanh.chungcu.entity.User;
import com.sanh.chungcu.enums.ResidentRelationType;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "apartment_residents")
@Getter
@Setter
public class ApartmentResident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Apartment apartment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "relation_type", length = 30)
    private ResidentRelationType relationType;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Column(name = "moved_in_at")
    private LocalDate movedInAt;

    @Column(name = "moved_out_at")
    private LocalDate movedOutAt;

}
