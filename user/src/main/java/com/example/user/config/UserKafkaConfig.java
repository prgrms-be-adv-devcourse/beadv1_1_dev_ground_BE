package com.example.user.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class UserKafkaConfig {

	@Value("${users.config.topic-partitions}")
	private int topic_partitions;

	@Value("${users.config.topic-replications}")
	private int topic_replications;

	@Value("${users.events.topic.join}")
	private String usersJoinEventsTopicName;

	@Value("${carts.events.topic.join}")
	private String cartsJoinUserEventsTopicName;

	@Value("${deposits.events.topic.join}")
	private String depositsJoinUserEventsTopicName;

	@Value("${carts.commands.topic.join}")
	private String cartsJoinUserCommandsTopicName;

	@Value("${deposits.commands.topic.join}")
	private String depositsJoinUserCommandTopicName;

	@Value("${users.commands.topic.join}")
	private String usersJoinCommandTopicName;

	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate(
		ProducerFactory<String, Object> producerFactory
	) {
		return new KafkaTemplate<>(producerFactory);
	}

	@Bean
	public NewTopic createUsersEventTopic() {
		return TopicBuilder.name(usersJoinEventsTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createCartsUserEventTopic() {
		return TopicBuilder.name(cartsJoinUserEventsTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createDepositsEventTopic() {
		return TopicBuilder.name(depositsJoinUserEventsTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createCartsCommandsTopic() {
		return TopicBuilder.name(cartsJoinUserCommandsTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createDepositsCommandsTopic() {
		return TopicBuilder
			.name(depositsJoinUserCommandTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createUsersCommandsTopic() {
		return TopicBuilder
			.name(usersJoinCommandTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}
}
