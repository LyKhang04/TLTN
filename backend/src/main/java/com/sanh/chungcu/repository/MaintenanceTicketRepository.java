package com.sanh.chungcu.repository;

import com.sanh.chungcu.entity.MaintenanceTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceTicketRepository extends JpaRepository<MaintenanceTicket, Integer> {
    List<MaintenanceTicket> findByAssignedTo_Id(Integer userId);
    List<MaintenanceTicket> findByApartment_Id(Integer apartmentId);
}
