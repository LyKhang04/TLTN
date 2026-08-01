package com.sanh.chungcu.repository;

import com.sanh.chungcu.entity.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessLogRepository extends JpaRepository<AccessLog, Integer> {
}
