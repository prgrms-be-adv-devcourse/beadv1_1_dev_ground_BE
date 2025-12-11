package io.devground.core.event.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatReadEvent {
    private String chatId;
    private Long readerCode;
    private LocalDateTime readAt;
}
