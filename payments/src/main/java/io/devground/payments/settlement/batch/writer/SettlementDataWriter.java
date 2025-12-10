package io.devground.payments.settlement.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import io.devground.payments.settlement.model.entity.Settlement;
import io.devground.payments.settlement.repository.SettlementRepository;
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
	}

}
