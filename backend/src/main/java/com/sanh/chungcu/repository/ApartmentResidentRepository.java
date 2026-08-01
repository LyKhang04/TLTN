package com.sanh.chungcu.repository;

import com.sanh.chungcu.entity.ApartmentResident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApartmentResidentRepository extends JpaRepository<ApartmentResident, Integer> {
    List<ApartmentResident> findByUser_Id(Integer userId);
}
