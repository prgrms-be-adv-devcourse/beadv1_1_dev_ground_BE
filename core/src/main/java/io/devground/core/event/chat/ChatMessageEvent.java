package io.devground.core.event.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEvent {
    private String chatId;
    private Long senderCode;
    private String message;
    private LocalDateTime createdAt;
}
