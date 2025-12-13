package com.example.chat.service;

import com.example.chat.model.event.ChatMessageEvent;
import com.example.chat.model.event.ChatReadEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = "${chats.event.topic.chat}", groupId = "chat-ws")
public class ChatEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaHandler
    public void onMessage(ChatMessageEvent event) {
        messagingTemplate.convertAndSend("/topic/chat/" + event.getChatId(), event);
    }

    @KafkaHandler
    public void onRead(ChatReadEvent event) {
        messagingTemplate.convertAndSend("/topic/chat/" + event.getChatId() + "/read", event);
    }

}
