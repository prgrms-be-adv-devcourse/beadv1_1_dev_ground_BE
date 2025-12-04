package com.example.chat.model.entity;

import io.devground.core.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chatMessages")
@NoArgsConstructor
@Getter
public class ChatMessages extends BaseEntity {

    @Id
    private Long id;

    private Long chatId;

    private String message;

    private Long senderCode;

    private Boolean isRead;

    @Builder
    public ChatMessages(Long chatId, Long senderCode, String message, Boolean isRead) {
        this.chatId = chatId;
        this.senderCode = senderCode;
        this.message = message;
        this.isRead = false;
    }


}
