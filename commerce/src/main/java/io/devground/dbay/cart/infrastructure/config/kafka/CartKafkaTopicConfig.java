package io.devground.dbay.cart.infrastructure.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class CartKafkaTopicConfig {

	@Value("${custom.kafka.config.topic-partitions}")
	private int topic_partitions;

	@Value("${custom.kafka.config.topic-replications}")
	private int topic_replications;

	@Value("${carts.event.topic.join}")
	private String cartsJoinEventTopicName;

	@Value("${carts.event.topic.purchase}")
	private String cartOrderEventTopicName;

	@Value("${carts.command.topic.join}")
	private String cartsJoinCommandTopicName;

	@Value("${carts.command.topic.purchase}")
	private String cartOrderCommandTopicName;

	private static final String DLT = ".DLT";

	@Bean
	public NewTopic createCartJoinEventTopic() {
		return TopicBuilder.name(cartsJoinEventTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic creatCartJoinCommandTopic() {
		return TopicBuilder.name(cartsJoinCommandTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic cartsOrderEventTopic() {
		return TopicBuilder.name(cartOrderEventTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic cartsOrderCommandTopic() {
		return TopicBuilder.name(cartOrderCommandTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createCartEventDltTopic() {
		return TopicBuilder.name(cartsJoinEventTopicName + DLT)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic creatCartCommandDltTopic() {
		return TopicBuilder.name(cartsJoinCommandTopicName + DLT)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic cartsOrderEventDltTopic() {
		return TopicBuilder.name(cartOrderEventTopicName + DLT)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic cartsOrderCommandDltTopic() {
		return TopicBuilder.name(cartOrderCommandTopicName + DLT)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}
}
