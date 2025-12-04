package com.example.chat.repository;

import com.example.chat.model.entity.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<ChatRoom, Long> {

}
