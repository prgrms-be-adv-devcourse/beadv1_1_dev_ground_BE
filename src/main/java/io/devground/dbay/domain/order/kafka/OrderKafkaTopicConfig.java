package io.devground.dbay.domain.order.kafka;

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

	@Value("${orders.event.topic.order}")
	private String orderEventTopicName;

	@Value("${orders.command.topic.order}")
	private String orderCommandTopicName;

	@Bean
	public NewTopic createOrderEventTopic() {
		return TopicBuilder.name(orderEventTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createOrderCommandTopic() {
		return TopicBuilder.name(orderCommandTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}
}
