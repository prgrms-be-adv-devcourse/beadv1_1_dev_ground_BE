package com.example.chat.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEvent {
    private String chatId;
    private String senderCode;
    private String message;
    private LocalDateTime createdAt;
}
