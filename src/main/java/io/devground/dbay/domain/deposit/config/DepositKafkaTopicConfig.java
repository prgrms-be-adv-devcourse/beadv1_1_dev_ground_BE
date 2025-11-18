package io.devground.dbay.domain.deposit.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class DepositKafkaTopicConfig {

	@Value("${custom.kafka.config.topic-partitions}")
	private int topicPartitions;

	@Value("${custom.kafka.config.topic-replications}")
	private int topicReplications;

	@Value("${deposits.event.topic.name}")
	private String depositEventTopicName;

	@Value("${deposits.command.topic.name}")
	private String depositCommandTopicName;

	@Value("${deposits.event.topic.join}")
	private String depositsJoinEventTopicName;

	@Value("${deposits.command.topic.join}")
	private String depositsJoinCommandTopicName;

	@Bean
	public NewTopic createDepositEventTopic() {
		return TopicBuilder.name(depositEventTopicName)
			.partitions(topicPartitions)
			.replicas(topicReplications)
			.build();
	}

	@Bean
	public NewTopic createDepositCommandTopic() {
		return TopicBuilder.name(depositCommandTopicName)
			.partitions(topicPartitions)
			.replicas(topicReplications)
			.build();
	}

	@Bean
	public NewTopic createDepositJoinEventTopic() {
		return TopicBuilder.name(depositsJoinEventTopicName)
			.partitions(topicPartitions)
			.replicas(topicReplications)
			.build();
	}

	@Bean
	public NewTopic createDepositJoinCommandTopic() {
		return TopicBuilder.name(depositsJoinCommandTopicName)
			.partitions(topicPartitions)
			.replicas(topicReplications)
			.build();
	}
}
