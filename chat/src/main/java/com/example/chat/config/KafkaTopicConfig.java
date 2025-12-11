package com.example.chat.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${custom.kafka.config.topic-partitions}")
    private int topic_partitions;

    @Value("${custom.kafka.config.topic-replications}")
    private int topic_replications;

    @Value("${chats.command.topic.chat}")
    private String chatsChatCommandTopicName;

    @Value("${chats.event.topic.chat}")
    private String chatChatEventTopicName;

    @Bean
    public NewTopic createChatChatsEventTopic() {
        return TopicBuilder.name(chatChatEventTopicName)
                .partitions(topic_partitions)
                .replicas(topic_replications)
                .build();
    }

    @Bean
    public NewTopic createChatsChatCommandTopic() {
        return TopicBuilder.name(chatsChatCommandTopicName)
                .partitions(topic_partitions)
                .replicas(topic_replications)
                .build();
    }
}
