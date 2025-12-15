package io.devground.chat.controller;

import io.devground.chat.client.ProductClient;
import io.devground.chat.client.UserClient;
import io.devground.chat.enums.ChatRoomStatus;
import io.devground.chat.model.dto.request.ChatRoomRequest;
import io.devground.chat.model.dto.response.ChatRoomSummary;
import io.devground.chat.model.entity.ChatMessages;
import io.devground.chat.model.event.ChatReadEvent;
import io.devground.chat.model.entity.ChatRoom;
import io.devground.chat.service.ChatMessageService;
import io.devground.chat.service.ChatRoomService;
import io.devground.chat.service.ChatEventProducer;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;

import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
@Tag(name = "ChatController")

public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final ChatEventProducer chatEventProducer;
    private final UserClient userClient;
    private final ProductClient productClient;


    //채팅방 생성
    @PostMapping("/rooms")
    public ChatRoom createOrGetRoom(
            @RequestHeader(value = "X-CODE", required = false) String userCode,
            @RequestBody ChatRoomRequest request
    ) {
        String buyerCode = request.getBuyerCode();
        if (buyerCode == null || buyerCode.isBlank()) {
            buyerCode = userCode; // fallback to header
        }
        if (buyerCode == null || buyerCode.isBlank()) {
            throw new ServiceException(ErrorCode.PARAMETER_INVALID, "X-CODE가 비어 있습니다.");
        }
        // 본인 상품 채팅 방지
        if (request.getSellerCode() != null && request.getSellerCode().equals(buyerCode)) {
            throw new ServiceException(ErrorCode.PARAMETER_INVALID, "본인 상품에는 채팅을 시작할 수 없습니다.");
        }
        return chatRoomService.getOrCreateRoom(
                request.getProductCode(),
                request.getSellerCode(),
                buyerCode
        );
    }

    //전체 메세지 조회
    @GetMapping("/rooms/{chatId}/messages")
    public List<ChatMessages> getMessages(
            @RequestHeader("X-CODE") String userCode,
            @PathVariable String chatId) {
        chatRoomService.getRoom(chatId); // 존재하는 chatId인지
        chatMessageService.markAsRead(chatId, userCode);
        chatEventProducer.sendReadEvent(new ChatReadEvent(chatId, userCode));
        return chatMessageService.getMessages(chatId);
    }

    @PostMapping("/rooms/{chatId}/read")
    public void markRead(
            @RequestHeader("X-CODE") String userCode,
            @PathVariable String chatId
    ) {
        chatRoomService.getRoom(chatId); // 존재하는 chatId인지 확인
        chatMessageService.markAsRead(chatId, userCode);
        chatEventProducer.sendReadEvent(new ChatReadEvent(chatId, userCode));
    }

    //채팅방 목록 (OPEN 상태, 참여자 기준)
    @GetMapping("/rooms")
    public List<ChatRoomSummary> listRooms(
            @RequestHeader("X-CODE") String userCode,
            @RequestParam(value = "status", defaultValue = "OPEN") String status
    ) {
        ChatRoomStatus roomStatus = ChatRoomStatus.valueOf(status.toUpperCase(Locale.ROOT));
        return chatRoomService.listOpenRoomsForUser(userCode);
    }

    //채팅 나가기
    @PostMapping("/rooms/{chatId}/leave")
    public ChatRoom leaveRoom(
            @RequestHeader("X-CODE") String userCode,
            @PathVariable String chatId
    ) {
        if (userCode == null || userCode.isBlank()) {
            throw new ServiceException(ErrorCode.PARAMETER_INVALID, "X-CODE 헤더가 비어 있습니다.");
        }
        userClient.getUser(userCode).throwIfNotSuccess();
        return chatRoomService.leaveRoom(chatId, userCode);
    }


}
