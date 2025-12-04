package com.example.chat.service;

import com.example.chat.enums.ChatRoomStatus;
import com.example.chat.model.entity.ChatRoom;
import com.example.chat.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final RoomRepository roomRepository;

    public ChatRoom getOrCreateRoom(Long productCode, Long sellerCode, Long buyerCode) {
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

}
