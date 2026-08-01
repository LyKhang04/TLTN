package com.sanh.chungcu.repository;

import com.sanh.chungcu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
