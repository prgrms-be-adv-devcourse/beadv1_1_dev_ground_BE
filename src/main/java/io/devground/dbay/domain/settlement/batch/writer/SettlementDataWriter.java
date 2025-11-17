package io.devground.dbay.domain.settlement.batch.writer;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.devground.core.events.settlement.SettlementCreatedSuccess;
import io.devground.dbay.domain.settlement.model.entity.Settlement;
import io.devground.dbay.domain.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Settlement 엔티티를 데이터베이스에 저장하고 Kafka 이벤트를 발행하는 Spring Batch ItemWriter
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementDataWriter implements ItemWriter<Settlement> {

	private final SettlementRepository settlementRepository;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${settlements.event.topic.name}")
	private String settlementsEventTopicName;

	/**
	 * 처리된 Settlement 엔티티들을 일괄 저장하고 Kafka 이벤트 발행
	 * Spring Batch가 청크 단위로 처리한 Settlement들을 한 번에 저장
	 */
	@Override
	public void write(Chunk<? extends Settlement> chunk) {
		log.info("Settlement 저장 시작: {} 건", chunk.size());

		// Settlement 저장
		settlementRepository.saveAll(chunk.getItems());
		log.info("Settlement 저장 완료: {} 건", chunk.size());

		// orderCode 기준으로 중복 제거 후 이벤트 발행
		List<String> orderCodes = chunk.getItems().stream()
			.map(Settlement::getOrderCode)
			.distinct()
			.toList();

		publishSettlementCreatedEvent(orderCodes);
	}

	/**
	 * Settlement 생성 성공 이벤트를 Kafka로 발행
	 * orderCode 리스트를 한 번에 발행
	 */
	private void publishSettlementCreatedEvent(List<String> orderCodes) {
		try {
			SettlementCreatedSuccess event = SettlementCreatedSuccess.builder()
				.orderCodes(orderCodes)
				.build();

			kafkaTemplate.send(settlementsEventTopicName, event);
			log.info("Settlement 생성 이벤트 발행 완료: orderCodes={}", orderCodes);

		} catch (Exception e) {
			log.error("Settlement 생성 이벤트 발행 실패: orderCodes={}", orderCodes, e);
			// todo: 이벤트 발행 실패시 정산 롤백 이벤트 처리필요.
		}
	}
}
