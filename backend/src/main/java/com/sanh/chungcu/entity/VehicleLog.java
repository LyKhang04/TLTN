package com.sanh.chungcu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sanh.chungcu.entity.User;
import com.sanh.chungcu.entity.Vehicle;
import com.sanh.chungcu.enums.LogDirection;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vehicle_logs")
@Getter
@Setter
public class VehicleLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "guard_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User guard;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", length = 30)
    private LogDirection direction;

    @Column(name = "gate", length = 20)
    private String gate;

    @Column(name = "logged_at")
    private LocalDateTime loggedAt;

}
