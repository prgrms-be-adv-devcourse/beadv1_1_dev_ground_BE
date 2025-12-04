package com.example.chat.model.entity;

import io.devground.core.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
public class ChatMessages extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private Boolean isRead;

    @Builder
    public ChatMessages(Long roomId, Long senderId, String message, Boolean isRead) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.message = message;
        this.isRead = false;
    }


}
