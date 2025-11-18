package io.devground.dbay.domain.payment.config;

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

	@Value("${payments.event.topic.order}")
	private String paymentOrderEventTopicName;

	@Value("${payments.command.topic.order}")
	private String paymentOrderCommandTopicName;

	@Bean
	public NewTopic createPaymentOrderEventTopic() {
		return TopicBuilder.name(paymentOrderEventTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}

	@Bean
	public NewTopic createPaymentCommandTopic() {
		return TopicBuilder.name(paymentOrderCommandTopicName)
			.partitions(topic_partitions)
			.replicas(topic_replications)
			.build();
	}
}
