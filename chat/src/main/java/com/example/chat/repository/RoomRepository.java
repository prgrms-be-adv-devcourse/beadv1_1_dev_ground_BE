package com.example.chat.repository;

import com.example.chat.model.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<ChatRoom, Integer> {



}
