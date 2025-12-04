package com.example.chat.model.entity;

import com.example.chat.enums.ChatRoomStatus;
import io.devground.core.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private Long buyerId;

    @Column(nullable = false)
    private ChatRoomStatus status;

    @Builder
    public ChatRoom(Long productId, Long sellerId, Long buyerId, ChatRoomStatus status) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.status = status;
    }




}
