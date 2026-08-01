package com.sanh.chungcu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sanh.chungcu.entity.User;
import com.sanh.chungcu.enums.NotificationScope;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User createdBy;

    @Column(name = "title", length = 150)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_scope", length = 30)
    private NotificationScope targetScope;

}
