package com.example.chat.service;

import com.example.chat.enums.ChatRoomStatus;
import com.example.chat.model.dto.response.ChatRoomSummary;
import com.example.chat.model.entity.ChatMessages;
import com.example.chat.model.entity.ChatRoom;
import com.example.chat.repository.MessageRepository;
import com.example.chat.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;

    public ChatRoom getOrCreateRoom(String productCode, String sellerCode, String buyerCode) {
        return roomRepository
                .findByProductCodeAndSellerCodeAndBuyerCode(productCode, sellerCode, buyerCode)
                .orElseGet(() -> roomRepository.save(
                        ChatRoom.builder()
                                .productCode(productCode)
                                .sellerCode(sellerCode)
                                .buyerCode(buyerCode)
                                .status(ChatRoomStatus.OPEN)
                                .build()
                ));

    }

    public ChatRoom getRoom(String chatId) {
        return roomRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + chatId));
    }


    //OPEN상태인 채팅방 조회
    public List<ChatRoomSummary> listOpenRoomsForUser(String userCode) {
        List<ChatRoom> rooms = roomRepository.findByStatusAndSellerCodeOrStatusAndBuyerCode(
                ChatRoomStatus.OPEN, userCode, ChatRoomStatus.OPEN, userCode
        );

        return rooms.stream().map(room -> {
            ChatMessages last = messageRepository.findFirstByChatIdOrderByCreatedAtDesc(room.getId());
            long unread = messageRepository.countByChatIdAndSenderCodeNotAndIsReadFalse(room.getId(), userCode);
            return ChatRoomSummary.builder()
                    .id(room.getId())
                    .productCode(room.getProductCode())
                    .sellerCode(room.getSellerCode())
                    .buyerCode(room.getBuyerCode())
                    .status(room.getStatus())
                    .lastMessage(last != null ? last.getMessage() : null)
                    .lastMessageAt(last != null ? last.getCreatedAt() : null)
                    .unreadCount(unread)
                    .build();
        }).collect(Collectors.toList());
    }

    public ChatRoom leaveRoom(String chatId, String userCode) {
        ChatRoom room = getRoom(chatId);
        boolean isParticipant = userCode.equals(room.getSellerCode()) || userCode.equals(room.getBuyerCode());
        if (!isParticipant) {
            throw new IllegalArgumentException("이 채팅방의 참여자가 아닙니다.");
        }
        room.close();
        return roomRepository.save(room);
    }


    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }



}

}
