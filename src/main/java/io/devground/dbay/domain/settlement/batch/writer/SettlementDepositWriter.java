package io.devground.dbay.domain.settlement.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.devground.core.commands.deposit.ChargeDeposit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementDepositWriter implements ItemWriter<ChargeDeposit> {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${deposits.command.topic.name}")
	private String depositsCommandTopicName;


	@Override
	public void write(Chunk<? extends ChargeDeposit> chunk) {
		log.info("입금 커맨드 전송 시작: {} 건", chunk.size());

		chunk.getItems().forEach(this::sendChargeDepositCommand);

		log.info("입금 커맨드 전송 완료: {} 건", chunk.size());
	}

	/**
	 * ChargeDeposit 커맨드를 Kafka로 전송
	 */
	private void sendChargeDepositCommand(ChargeDeposit command) {
		try {
			kafkaTemplate.send(depositsCommandTopicName, command);
			log.info("입금 커맨드 전송 성공: userCode={}, amount={}, type={}",
				command.userCode(), command.amount(), command.type());

		} catch (Exception e) {

			//todo : 입금 커맨트 전송실패 처리
			log.error("입금 커맨드 전송 실패: userCode={}, amount={}",
				command.userCode(), command.amount(), e);
		}
	}
}