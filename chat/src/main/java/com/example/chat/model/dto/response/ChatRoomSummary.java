package com.example.chat.model.dto.response;

import com.example.chat.enums.ChatRoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomSummary {
    private String id;
    private String productCode;
    private String productTitle;
    private String sellerCode;
    private String buyerCode;
    private ChatRoomStatus status;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private long unreadCount;
}
