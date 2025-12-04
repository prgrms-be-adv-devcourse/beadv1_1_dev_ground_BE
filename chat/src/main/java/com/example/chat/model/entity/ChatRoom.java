package com.example.chat.model.entity;

import com.example.chat.enums.ChatRoomStatus;
import io.devground.core.model.entity.BaseEntity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "chatRooms")
@NoArgsConstructor
@Getter
public class ChatRoom extends BaseEntity {

    @Id
    private String id;

    private String chatCode = UUID.randomUUID().toString();

    private Long productCode;

    private Long sellerCode;

    private Long buyerCode;

    private ChatRoomStatus status;

    @Builder
    public ChatRoom(Long productCode, Long sellerCode, Long buyerCode, ChatRoomStatus status) {
        this.productCode = productCode;
        this.sellerCode = sellerCode;
        this.buyerCode = buyerCode;
        this.status = status;
    }




}
