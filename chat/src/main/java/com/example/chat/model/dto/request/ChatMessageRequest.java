package com.example.chat.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    private String message;
    private String senderCode;
    private String chatId;

    @Override
    public String toString() {
        return "ChatMessageRequest{" +
                "message='" + message + '\'' +
                ", senderCode='" + senderCode + '\'' +
                ", chatId='" + chatId + '\'' +
                '}';
    }
}
