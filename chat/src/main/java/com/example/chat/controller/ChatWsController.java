package com.example.chat.controller;

import com.example.chat.client.UserClient;
import com.example.chat.model.dto.request.ChatMessageRequest;
import com.example.chat.model.entity.ChatMessages;
import com.example.chat.model.event.ChatMessageEvent;
import com.example.chat.service.ChatEventProducer;
import com.example.chat.service.ChatMessageService;
import com.example.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWsController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final UserClient userClient;
    private final ChatEventProducer chatEventProducer;

    @MessageMapping("/chat/messages")
    public void handleMessage(
            @Header("X-CODE") String userCode,
            ChatMessageRequest request
    ) {
        log.info("메세지 받음: {}", request);
        String chatId = request.getChatId();
        if (chatId == null || chatId.isBlank()) {
            throw new IllegalArgumentException("chatId가 비어 있습니다.");
        }
        validateUser(userCode, request.getSenderCode());
        chatRoomService.getRoom(chatId);

        ChatMessages saved = chatMessageService.sendMessage(chatId, request.getSenderCode(), request.getMessage());
        chatEventProducer.sendMessageEvent(new ChatMessageEvent(
                saved.getChatId(),
                saved.getSenderCode(),
                saved.getMessage(),
                saved.getCreatedAt()
        ));
    }

    private void validateUser(String userCodeHeader, String userCodeBody) {
        if (userCodeBody == null || userCodeBody.isBlank()) {
            throw new IllegalArgumentException("senderCode가 비어 있습니다.");
        }
        if (userCodeHeader == null || userCodeHeader.isBlank()) {
            throw new IllegalArgumentException("X-CODE 헤더가 비어 있습니다.");
        }

        userClient.getUser(userCodeHeader).throwIfNotSuccess();
    }

}
