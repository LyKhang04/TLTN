package com.sanh.chungcu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sanh.chungcu.entity.User;
import com.sanh.chungcu.entity.VisitorRegistration;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "visitor_checkins")
@Getter
@Setter
public class VisitorCheckin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "visitor_registration_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private VisitorRegistration visitorRegistration;

    @ManyToOne
    @JoinColumn(name = "guard_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User guard;

    @Column(name = "checkin_time")
    private LocalDateTime checkinTime;

    @Column(name = "checkout_time")
    private LocalDateTime checkoutTime;

}
