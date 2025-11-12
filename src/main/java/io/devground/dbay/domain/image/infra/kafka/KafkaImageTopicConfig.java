package io.devground.dbay.domain.image.infra.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import io.devground.dbay.domain.image.config.ImageTopicProperties;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KafkaImageTopicConfig {

	@Value("${custom.kafka.config.topic-partitions}")
	private int partitions;

	@Value("${custom.kafka.config.topic-replications}")
	private int replicas;

	private final ImageTopicProperties imageTopicProperties;

	@Bean
	public NewTopic imagesPushTopic() {

		return TopicBuilder.name(imageTopicProperties.getPush())
			.partitions(partitions)
			.replicas(replicas)
			.build();
	}

	@Bean
	public NewTopic imagesDeleteTopic() {

		return TopicBuilder.name(imageTopicProperties.getDelete())
			.partitions(partitions)
			.replicas(replicas)
			.build();
	}
}
