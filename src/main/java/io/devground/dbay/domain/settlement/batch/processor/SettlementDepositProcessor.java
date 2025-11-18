package io.devground.dbay.domain.settlement.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import io.devground.core.commands.deposit.SettlementChargeDeposit;
import io.devground.dbay.domain.settlement.model.entity.Settlement;
import lombok.extern.slf4j.Slf4j;

/**
 * Settlement를 SettlementChargeDeposit 커맨드로 변환하는 Processor
 */
@Slf4j
@Component
public class SettlementDepositProcessor implements ItemProcessor<Settlement, SettlementChargeDeposit> {

	/**
	 * Settlement를 SettlementChargeDeposit 커맨드로 변환
	 * 판매자(sellerCode)에게 정산 금액(settlementBalance)을 입금
	 */
	@Override
	public SettlementChargeDeposit process(Settlement settlement) {
		log.info("SettlementChargeDeposit 커맨드 생성 중: sellerCode={}, settlementBalance={}, orderCode={}",
			settlement.getSellerCode(), settlement.getSettlementBalance(), settlement.getOrderCode());

		SettlementChargeDeposit command = new SettlementChargeDeposit(
			settlement.getSellerCode(),        // 판매자 코드
			settlement.getSettlementBalance(), // 정산 금액
			settlement.getOrderCode()          // 주문 코드
		);

		log.info("SettlementChargeDeposit 커맨드 생성 완료: userCode={}, amount={}, orderCode={}",
			command.userCode(), command.amount(), command.orderCode());

		return command;
	}
}