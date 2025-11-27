package io.devground.dbay.order.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class OrderKafkaTopicConfig {

	@Value("${custom.kafka.config.topic-partitions}")
	private int topic_partitions;

	@Value("${custom.kafka.config.topic-replications}")
	private int topic_replications;

	@Value("${orders.event.topic.purchase}")
	private String orderPurchaseEventTopicName;

	@Value("${orders.command.topic.purchase}")
	private String orderPurchaseCommandTopicName;

	private static final String DLT = ".DLT";

	@Bean
	public NewTopic orderPurchaseEventTopic() {
		return TopicBuilder.name(orderPurchaseEventTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic orderPurchaseCommandTopic() {
		return TopicBuilder.name(orderPurchaseCommandTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic orderPurchaseEventDltTopic() {
		return TopicBuilder.name(orderPurchaseEventTopicName + DLT)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic orderPurchaseCommandDltTopic() {
		return TopicBuilder.name(orderPurchaseCommandTopicName + DLT)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}
}
