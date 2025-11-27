package io.devground.common.kafka;

import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.DeserializationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO: 에러처리 전략 재확인
 * 1. Retry 는 우선 최대 5회, 지수 백오프
 * 2. DLT (Dead Letter Topic) : 재시도 실패할 때 전송
 * 3. Non_Retryable : 역직렬화 실패 등은 바로 DLT 전송
 * 4. DLT 토픽 생성 필요 -> 따로 생성함
 * TODO: 5. Retry 토픽도 추가로 필요한가?
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaErrorHandler {

	@Value("${custom.kafka.config.topic-partitions}")
	private Integer topicPartitions;

	private static final String DLT = ".DLT";
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Bean
	public CommonErrorHandler commonErrorHandler() {

		DeadLetterPublishingRecoverer recover = new DeadLetterPublishingRecoverer(
			kafkaTemplate,
			(record, exception) -> {
				String topic = record.topic();
				String dltTopic = topic + DLT;

				log.error("Kafka 메시지 DLT 전송 - Topic: {} -> {}, Key: {}, Offset: {}, Exception: ",
					topic, dltTopic, record.key(), record.offset(), exception);

				return new TopicPartition(dltTopic, record.partition());
			}
		);

		ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(5);
		backOff.setInitialInterval(1000L);
		backOff.setMultiplier(2.0);
		backOff.setMaxInterval(20000L);

		DefaultErrorHandler errorHandler = new DefaultErrorHandler(recover, backOff);

		errorHandler.addNotRetryableExceptions(DeserializationException.class);

		errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
			log.warn("Kafka 메시지 재시도 - Topic: {}, Key: {}, Offset: {}, Attempt: {}, Exception: {}",
				record.topic(), record.key(), record.offset(), deliveryAttempt, ex.getMessage()
			);
		});

		return errorHandler;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
		ConsumerFactory<String, Object> consumerFactory,
		CommonErrorHandler commonErrorHandler
	) {

		ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();

		factory.setConsumerFactory(consumerFactory);
		factory.setCommonErrorHandler(commonErrorHandler);
		factory.setConcurrency(topicPartitions);

		return factory;
	}
}
