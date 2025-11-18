package io.devground.dbay.domain.settlement.service.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import io.devground.core.event.deposit.SettlementDepositChargedSuccess;
import io.devground.dbay.domain.settlement.saga.SettlementSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Deposit 이벤트를 수신하여 Settlement Saga Orchestrator에 전달
 */
@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(topics = {
	"${deposits.event.topic.name}"
})
public class SettlementEventHandler {

	private final SettlementSagaOrchestrator sagaOrchestrator;

	/**
	 * 정산 예치금 충전 성공 이벤트 수신
	 */
	@KafkaHandler
	public void handleEvent(@Payload SettlementDepositChargedSuccess event) {
		log.info("정산 예치금 충전 성공 이벤트 수신: userCode={}, amount={}, orderCode={}",
			event.userCode(), event.amount(), event.orderCode());

		try {
			sagaOrchestrator.handleDepositChargeSuccess(event);

		} catch (Exception e) {
			log.error("정산 예치금 충전 성공 이벤트 처리 실패: orderCode={}", event.orderCode(), e);
		}
	}

}