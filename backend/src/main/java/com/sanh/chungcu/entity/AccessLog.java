package com.sanh.chungcu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sanh.chungcu.entity.ResidentCard;
import com.sanh.chungcu.enums.LogDirection;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "access_logs")
@Getter
@Setter
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "card_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ResidentCard card;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", length = 30)
    private LogDirection direction;

    @Column(name = "gate", length = 20)
    private String gate;

    @Column(name = "logged_at")
    private LocalDateTime loggedAt;

}
