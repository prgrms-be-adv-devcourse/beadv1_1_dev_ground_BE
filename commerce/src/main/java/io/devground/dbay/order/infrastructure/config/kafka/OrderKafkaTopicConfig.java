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

	@Value("${orders.event.topic.purchase}")
	private String orderPurchaseEventTopicName;

	@Value("${orders.command.topic.purchase}")
	private String orderPurchaseCommandTopicName;

	@Value("${deposits.event.topic.purchase}")
	private String depositPurchaseEventTopicName;

	@Value("${deposits.command.topic.purchase}")
	private String depositPurchaseCommandTopicName;

	@Value("${payments.event.topic.purchase}")
	private String paymentsPurchaseEventTopicName;

	@Value("${payments.command.topic.purchase}")
	private String paymentsPurchaseCommandTopicName;

	@Value("${payments.event.topic.name}")
	private String paymentsEventTopicName;

	@Value("${products.command.purchase}")
	private String productPurchaseCommandTopicName;

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

	@Bean
	public NewTopic depositPurchaseEventTopic() {
		return TopicBuilder.name(depositPurchaseEventTopicName)
				.partitions(topic_partitions)
				.replicas(topic_replications)
				.build();
	}

	@Bean
	public NewTopic depositPurchaseCommandTopic() {
		return TopicBuilder.name(depositPurchaseCommandTopicName)
				.partitions(topic_partitions)
				.replicas(topic_replications)
				.build();
	}

	@Bean
	public NewTopic depositPurchaseEventDltTopic() {
		return TopicBuilder.name(depositPurchaseEventTopicName + DLT)
				.partitions(topic_partitions)
				.replicas(topic_replications)
				.build();
	}

	@Bean
	public NewTopic depositPurchaseCommandDltTopic() {
		return TopicBuilder.name(depositPurchaseCommandTopicName + DLT)
				.partitions(topic_partitions)
				.replicas(topic_replications)
				.build();
	}

	@Bean
	public NewTopic paymentPurchaseEventTopic() {
		return TopicBuilder.name(paymentsPurchaseEventTopicName)
				.partitions(topic_partitions)
				.replicas(topic_replications)
				.build();
	}

	@Bean
	public NewTopic paymentPurchaseCommandTopic() {
		return TopicBuilder.name(paymentsPurchaseCommandTopicName)
				.partitions(topic_partitions)
				.replicas(topic_replications)
				.build();
	}

	@Bean
	public NewTopic paymentPurchaseEventDltTopic() {
		return TopicBuilder.name(paymentsPurchaseEventTopicName + DLT)
				.partitions(topic_partitions)
				.replicas(topic_replications)
				.build();
	}

	@Bean
	public NewTopic paymentPurchaseCommandDltTopic() {
		return TopicBuilder.name(paymentsPurchaseCommandTopicName + DLT)
				.partitions(topic_partitions)
				.replicas(topic_replications)
				.build();
	}

	@Bean
	public NewTopic paymentEventTopic() {
		return TopicBuilder.name(paymentsEventTopicName)
				.partitions(topic_partitions)
				.replicas(topic_replications)
				.build();
	}

	@Bean
	public NewTopic paymentEventDltTopic() {
		return TopicBuilder.name(paymentsEventTopicName + DLT)
				.partitions(topic_partitions)
				.replicas(topic_replications)
				.build();
	}

	@Bean
	public NewTopic productPurchaseCommandTopic() {
		return TopicBuilder.name(productPurchaseCommandTopicName)
				.partitions(topic_partitions)
				.replicas(topic_replications)
				.build();
	}

	@Bean
	public NewTopic productPurchaseCommandDltTopic() {
		return TopicBuilder.name(productPurchaseCommandTopicName + DLT)
				.partitions(topic_partitions)
				.replicas(topic_replications)
				.build();
	}
}
