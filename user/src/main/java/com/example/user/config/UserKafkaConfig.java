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

	@Value("${users.events.topic.name}")
	private String usersEventsTopicName;

	@Value("${carts.events.topic.name}")
	private String cartsEventsTopicName;

	@Value("${deposits.events.topic.name}")
	private String depositsEventsTopicName;

	@Value("${carts.commands.topic.name}")
	private String cartsCommandsTopicName;

	@Value("${deposits.commands.topic.name}")
	private String depositsCommandTopicName;

	@Value("${users.commands.topic.name}")
	private String usersCommandTopicName;

	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate(
		ProducerFactory<String, Object> producerFactory
	) {
		return new KafkaTemplate<>(producerFactory);
	}

	@Bean
	public NewTopic createUsersEventTopic() {
		return TopicBuilder.name(usersEventsTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createCartsEventTopic() {
		return TopicBuilder.name(cartsEventsTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createDepositsEventTopic() {
		return TopicBuilder.name(depositsEventsTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createCartsCommandsTopic() {
		return TopicBuilder.name(cartsCommandsTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createDepositsCommandsTopic() {
		return TopicBuilder
			.name(depositsCommandTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createUsersCommandsTopic() {
		return TopicBuilder
			.name(usersCommandTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}
}
