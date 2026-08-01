package com.sanh.chungcu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sanh.chungcu.entity.Incident;
import com.sanh.chungcu.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "incident_logs")
@Getter
@Setter
public class IncidentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "incident_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Incident incident;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User updatedBy;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "note", length = 255)
    private String note;

}
