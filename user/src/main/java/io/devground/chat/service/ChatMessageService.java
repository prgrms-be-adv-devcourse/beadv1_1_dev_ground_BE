package io.devground.chat.service;

import io.devground.chat.client.UserClient;
import io.devground.chat.model.dto.request.ChatMessageRequest;
import io.devground.chat.model.entity.ChatMessages;
import io.devground.chat.model.entity.ChatRoom;
import io.devground.chat.repository.MessageRepository;
import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomService chatRoomService;
    private final UserClient userClient;

    public ChatMessages sendValidatedMessage(String userCodeHeader, ChatMessageRequest request) {
        if (request == null) {
            throw new ServiceException(ErrorCode.CHAT_MESSAGE_MISSING);
        }
        String chatId = request.getChatId();
        String senderCode = request.getSenderCode();

        if (chatId == null || chatId.isBlank()) {
            throw new ServiceException(ErrorCode.CHAT_ID_MISSING);
        }
        if (senderCode == null || senderCode.isBlank()) {
            throw new ServiceException(ErrorCode.SENDER_CODE_MISSING);
        }
        if (userCodeHeader == null || userCodeHeader.isBlank()) {
            throw new ServiceException(ErrorCode.XCODE_NOT_FOUND);
        }

        // 사용자 존재 확인
        userClient.getUser(userCodeHeader).throwIfNotSuccess();

        // 방 존재 및 참여자 여부 확인
        ChatRoom room = chatRoomService.getRoom(chatId);
        boolean isParticipant = userCodeHeader.equals(room.getSellerCode()) || userCodeHeader.equals(room.getBuyerCode());
        if (!isParticipant) {
            throw new ServiceException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }

        return sendMessage(chatId, senderCode, request.getMessage());
    }

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
