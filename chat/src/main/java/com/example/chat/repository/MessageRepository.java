package com.example.chat.repository;

import com.example.chat.model.entity.ChatMessages;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<ChatMessages, String> {

    List<ChatMessages> findByChatIdOrderByCreatedAtAsc(String chatId);

    List<ChatMessages> findByChatIdAndSenderCodeNotAndIsReadFalse(String chatId, String senderCode);

    ChatMessages findFirstByChatIdOrderByCreatedAtDesc(String chatId);

    long countByChatIdAndSenderCodeNotAndIsReadFalse(String chatId, String senderCode);
}
