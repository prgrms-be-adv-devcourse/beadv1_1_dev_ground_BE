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
	private String cartsUserEventsTopicName;

	@Value("${deposits.events.topic.name}")
	private String depositsUserEventsTopicName;

	@Value("${carts.commands.topic.name}")
	private String cartsUserCommandsTopicName;

	@Value("${deposits.commands.topic.name}")
	private String depositsUserCommandTopicName;

	@Value("${users.commands.topic.name}")
	private String usersUserCommandTopicName;

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
	public NewTopic createCartsUserEventTopic() {
		return TopicBuilder.name(cartsUserEventsTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createDepositsEventTopic() {
		return TopicBuilder.name(depositsUserEventsTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createCartsCommandsTopic() {
		return TopicBuilder.name(cartsUserCommandsTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createDepositsCommandsTopic() {
		return TopicBuilder
			.name(depositsUserCommandTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createUsersCommandsTopic() {
		return TopicBuilder
			.name(usersUserCommandTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

}
