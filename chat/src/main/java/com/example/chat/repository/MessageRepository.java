package com.example.chat.repository;

import com.example.chat.model.entity.ChatMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessages, Integer> {

   // List<ChatMessages> findByRoomIdOrderByCreatedAtAsc(String roomId);

}
