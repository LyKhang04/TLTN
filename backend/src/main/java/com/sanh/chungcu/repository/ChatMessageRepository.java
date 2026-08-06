package com.sanh.chungcu.repository;

import com.sanh.chungcu.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findByUser_IdOrderByCreatedAtAsc(Integer userId);
}
