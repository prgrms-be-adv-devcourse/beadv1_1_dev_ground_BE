package com.example.chat.service;

import com.example.chat.model.event.ChatMessageEvent;
//import com.example.chat.model.event.ChatReadEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChatEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String chatChatEventTopicName;

    public ChatEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${chats.event.topic.chat}") String chatChatEventTopicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.chatChatEventTopicName = chatChatEventTopicName;
    }

    public void sendMessageEvent(ChatMessageEvent event) {
        kafkaTemplate.send(chatChatEventTopicName, event.getChatId(), event);
    }

}
