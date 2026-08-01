package com.sanh.chungcu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sanh.chungcu.entity.Apartment;
import com.sanh.chungcu.entity.User;
import com.sanh.chungcu.enums.CardStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "resident_cards")
@Getter
@Setter
public class ResidentCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Apartment apartment;

    @Column(name = "card_code", length = 50)
    private String cardCode;

    @Column(name = "card_type", length = 30)
    private String cardType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private CardStatus status;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

}
