package io.devground.dbay.domain.cart.kafka;

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

	@Value("${carts.event.topic.name}")
	private String cartEventTopicName;

	@Value("${carts.event.topic.user}")
	private String cartUserEventTopicName;

	@Value("${carts.command.topic.name}")
	private String cartCommandTopicName;

	@Value("${carts.command.topic.user}")
	private String cartUserCommandTopicName;

	private static final String DLT = ".DLT";

	@Bean
	public NewTopic createCartEventTopic() {
		return TopicBuilder.name(cartEventTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic creatCartCommandTopic() {
		return TopicBuilder.name(cartCommandTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic cartsUserEventTopic() {
		return TopicBuilder.name(cartUserEventTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic cartsUserCommandTopic() {
		return TopicBuilder.name(cartUserCommandTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createCartEventDltTopic() {
		return TopicBuilder.name(cartEventTopicName + DLT)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic creatCartCommandDltTopic() {
		return TopicBuilder.name(cartCommandTopicName + DLT)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic cartsUserEventDltTopic() {
		return TopicBuilder.name(cartUserEventTopicName + DLT)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic cartsUserCommandDltTopic() {
		return TopicBuilder.name(cartUserCommandTopicName + DLT)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}
}
