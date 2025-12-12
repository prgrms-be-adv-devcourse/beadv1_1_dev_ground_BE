package com.example.chat.service;

import com.example.chat.model.entity.ChatMessages;
import com.example.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final MessageRepository messageRepository;

    public ChatMessages sendMessage(String chatId, String senderCode, String message) {
        return messageRepository.save(
                ChatMessages.builder()
                        .chatId(chatId)
                        .senderCode(senderCode)
                        .message(message)
                        .createdAt(LocalDateTime.now())
                        .isRead(false)
                        .build()
        );
    }

    public List<ChatMessages> getMessages(String chatId) {
        return messageRepository.findByChatIdOrderByCreatedAtAsc(chatId);
    }

    public List<ChatMessages> markAsRead(String chatId, String readerCode) {
        List<ChatMessages> unread = messageRepository.findByChatIdAndSenderCodeNotAndIsReadFalse(chatId, readerCode);
        LocalDateTime readAt = LocalDateTime.now();
        unread.forEach(msg -> msg.markRead(readAt));
        return messageRepository.saveAll(unread);
    }


}
