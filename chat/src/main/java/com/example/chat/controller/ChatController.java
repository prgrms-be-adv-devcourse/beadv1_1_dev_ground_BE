package com.example.chat.controller;

import com.example.chat.client.ProductClient;
import com.example.chat.client.UserClient;
import com.example.chat.enums.ChatRoomStatus;
import com.example.chat.model.dto.request.ChatRoomRequest;
import com.example.chat.model.dto.response.ChatRoomSummary;
import com.example.chat.model.entity.ChatMessages;
import com.example.chat.model.entity.ChatRoom;
import com.example.chat.service.ChatMessageService;
import com.example.chat.service.ChatRoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
@Tag(name = "ChatController")

public class ChatController {

      private final ChatRoomService chatRoomService;


    //채팅방 생성
    @PostMapping("/rooms")
    public ChatRoom createOrGetRoom(@RequestBody ChatRoomRequest request) {
        return chatRoomService.getOrCreateRoom(
                request.getProductCode(),
                request.getSellerCode(),
                request.getBuyerCode()
        );
    }

    //전체 메세지 조회
    @GetMapping("/rooms/{chatId}/messages")
    public List<ChatMessages> getMessages(
            @RequestHeader("X-CODE") String userCode,
            @PathVariable String chatId) {
        chatRoomService.getRoom(chatId); // 존재하는 chatId인지
        chatMessageService.markAsRead(chatId, userCode);
        return chatMessageService.getMessages(chatId);
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


}
