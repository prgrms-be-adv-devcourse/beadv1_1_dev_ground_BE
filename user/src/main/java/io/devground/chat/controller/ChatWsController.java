package io.devground.chat.controller;

import io.devground.chat.client.UserClient;
import io.devground.chat.model.dto.request.ChatMessageRequest;
import io.devground.chat.model.entity.ChatMessages;
import io.devground.chat.model.event.ChatMessageEvent;
import io.devground.chat.service.ChatEventProducer;
import io.devground.chat.service.ChatMessageService;
import io.devground.chat.service.ChatRoomService;
import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
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
            throw new ServiceException(ErrorCode.CHAT_ID_MISSING);
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
            throw new ServiceException(ErrorCode.SENDER_CODE_MISSING);
        }
        if (userCodeHeader == null || userCodeHeader.isBlank()) {
            throw new ServiceException(ErrorCode.XCODE_NOT_FOUND);
        }

        userClient.getUser(userCodeHeader).throwIfNotSuccess();
    }

}
