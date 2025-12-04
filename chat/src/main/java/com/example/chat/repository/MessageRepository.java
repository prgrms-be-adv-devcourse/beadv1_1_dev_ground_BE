package com.example.chat.repository;

import com.example.chat.model.entity.ChatMessages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<ChatMessages, Integer> {
}
