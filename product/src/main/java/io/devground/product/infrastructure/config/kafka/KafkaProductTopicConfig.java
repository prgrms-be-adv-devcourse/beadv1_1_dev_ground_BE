package io.devground.product.infrastructure.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KafkaProductTopicConfig {

	@Value("${custom.kafka.config.topic-partitions}")
	private int partitions;

	@Value("${custom.kafka.config.topic-replications}")
	private int replicas;

	private final ProductTopicProperties productTopicProperties;

	@Bean
	public NewTopic productsImagePushTopic() {

		return TopicBuilder.name(productTopicProperties.getImage().getPush())
			.partitions(partitions)
			.replicas(replicas)
			.build();
	}

	@Bean
	public NewTopic productsImageDeleteTopic() {

		return TopicBuilder.name(productTopicProperties.getImage().getDelete())
			.partitions(partitions)
			.replicas(replicas)
			.build();
	}

	@Bean
	public NewTopic productsImagePushDltTopic() {

		return TopicBuilder.name(productTopicProperties.getImage().getPushDlt())
			.partitions(partitions)
			.replicas(replicas)
			.build();
	}

	@Bean
	public NewTopic productsImageDeleteDltTopic() {

		return TopicBuilder.name(productTopicProperties.getImage().getDeleteDlt())
			.partitions(partitions)
			.replicas(replicas)
			.build();
	}
}
