package io.devground.payments.settlement.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import io.devground.core.commands.deposit.SettlementChargeDeposit;
import io.devground.payments.settlement.saga.SettlementSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementDepositWriter implements ItemWriter<SettlementChargeDeposit> {

	private final SettlementSagaOrchestrator sagaOrchestrator;

	@Override
	public void write(Chunk<? extends SettlementChargeDeposit> chunk) {
		log.info("정산 입금 Saga 시작: {} 건", chunk.size());

		chunk.getItems().forEach(this::startSettlementDepositChargeSaga);

		log.info("정산 입금 Saga 시작 완료: {} 건", chunk.size());
	}

	/**
	 * SettlementChargeDeposit 커맨드를 Saga Orchestrator를 통해 발행
	 */
	private void startSettlementDepositChargeSaga(SettlementChargeDeposit command) {
		try {
			sagaOrchestrator.startSettlementDepositChargeSaga(command);

			log.info("정산 입금 Saga 시작 성공: userCode={}, amount={}, orderCode={}",
				command.userCode(), command.amount(), command.orderCode());

		} catch (Exception e) {
			log.error("정산 입금 Saga 시작 실패: userCode={}, amount={}, orderCode={}",
				command.userCode(), command.amount(), command.orderCode(), e);

			// todo: Saga 시작 실패 시 보상 처리 (2차 고도화)
		}
	}
}
