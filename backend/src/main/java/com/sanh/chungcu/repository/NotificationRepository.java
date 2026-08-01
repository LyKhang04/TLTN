package com.sanh.chungcu.repository;

import com.sanh.chungcu.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}
