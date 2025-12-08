package io.devground.payments.payment.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class PaymentKafkaConfig {
	@Value("${custom.kafka.config.topic-partitions}")
	private int topic_partitions;

	@Value("${custom.kafka.config.topic-replications}")
	private int topic_replications;

	@Value("${payments.event.topic.purchase}")
	private String paymentOrderEventTopicName;

	@Value("${payments.command.topic.purchase}")
	private String paymentOrderCommandTopicName;

	@Value("${payments.event.topic.name}")
	private String paymentEventTopicName;

	@Value("${payments.command.topic.name}")
	private String paymentCommandTopicName;

	@Value("${deposits.command.topic.name}")
	private String depositCommandTopicName;

	@Value("${deposits.event.topic.name}")
	private String depositEventTopicName;

	@Bean
	public NewTopic createPaymentPurchaseEventTopic() {
		return TopicBuilder.name(paymentOrderEventTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createPaymentPurchaseCommandTopic() {
		return TopicBuilder.name(paymentOrderCommandTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createPaymentEventTopic() {
		return TopicBuilder.name(paymentEventTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createPaymentCommandTopic() {
		return TopicBuilder.name(paymentCommandTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createDepositCommandTopic() {
		return TopicBuilder.name(depositCommandTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createDepositEventTopic() {
		return TopicBuilder.name(depositEventTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}
}

