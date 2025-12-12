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


    }

}
