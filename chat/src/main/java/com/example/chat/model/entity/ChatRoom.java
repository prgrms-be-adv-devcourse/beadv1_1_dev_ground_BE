package com.example.chat.model.entity;

import com.example.chat.enums.ChatRoomStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "chatRooms")
@NoArgsConstructor
@Getter
//@AllArgsConstructor
public class ChatRoom  {

    @Id
    private String id;

    private String chatCode = UUID.randomUUID().toString();

    private String productCode;

    private String sellerCode;

    private String buyerCode;

    private ChatRoomStatus status;

    @Builder
    public ChatRoom(String productCode, String sellerCode, String buyerCode, ChatRoomStatus status) {
        this.id = UUID.randomUUID().toString();
        this.productCode = productCode;
        this.chatCode = UUID.randomUUID().toString();
        this.sellerCode = sellerCode;
        this.buyerCode = buyerCode;
        this.status = status;
    }

    public void close() {
        this.status = ChatRoomStatus.CLOSED;
    }

}
