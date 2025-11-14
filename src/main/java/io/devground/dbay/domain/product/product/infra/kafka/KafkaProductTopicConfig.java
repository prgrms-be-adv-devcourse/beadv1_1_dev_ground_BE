package io.devground.dbay.domain.product.product.infra.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import io.devground.dbay.domain.product.product.config.ProductTopicProperties;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KafkaProductTopicConfig {

	@Value("${custom.kafka.config.topic-partitions}")
	private int partitions;

	@Value("${custom.kafka.config.topic-replications}")
	private int replicas;

	private static final String DLT = ".DLT";

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

		return TopicBuilder.name(productTopicProperties.getImage().getPush() + DLT)
			.partitions(partitions)
			.replicas(replicas)
			.build();
	}

	@Bean
	public NewTopic productsImageDeleteDltTopic() {

		return TopicBuilder.name(productTopicProperties.getImage().getDelete() + DLT)
			.partitions(partitions)
			.replicas(replicas)
			.build();
	}
}
