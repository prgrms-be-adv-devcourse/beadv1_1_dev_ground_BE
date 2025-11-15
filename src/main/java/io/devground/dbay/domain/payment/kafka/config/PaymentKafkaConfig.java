package io.devground.dbay.domain.payment.kafka.config;

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

	@Value("${payments.event.topic.name}")
	private String paymentEventTopicName;

	@Value("${payments.command.topic.name}")
	private String paymentCommandTopicName;

	@Bean
	public NewTopic createPaymentEventTopic() {
		return TopicBuilder.name(paymentEventTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic creatPaymentCommandTopic() {
		return TopicBuilder.name(paymentCommandTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}
}