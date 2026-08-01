package com.sanh.chungcu.repository;

import com.sanh.chungcu.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Integer> {
    List<Incident> findByReporter_Id(Integer reporterId);
}
