package io.devground.dbay.domain.settlement.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import io.devground.core.commands.deposit.ChargeDeposit;
import io.devground.core.model.vo.DepositHistoryType;
import io.devground.dbay.domain.settlement.model.entity.Settlement;
import lombok.extern.slf4j.Slf4j;

/**
 * Settlement를 Deposit Charge 커맨드로 변환하는 Processor
 */
@Slf4j
@Component
public class SettlementDepositProcessor implements ItemProcessor<Settlement, ChargeDeposit> {

	/**
	 * Settlement를 ChargeDeposit 커맨드로 변환
	 * 판매자(sellerCode)에게 정산 금액(settlementBalance)을 입금
	 */
	@Override
	public ChargeDeposit process(Settlement settlement) {
		log.info("ChargeDeposit 커맨드 생성 중: sellerCode={}, settlementBalance={}",
			settlement.getSellerCode(), settlement.getSettlementBalance());

		ChargeDeposit command = new ChargeDeposit(
			settlement.getSellerCode(),  // 판매자 코드
			settlement.getSettlementBalance(),  // 정산 금액
			DepositHistoryType.SETTLEMENT  // 정산 타입
		);

		log.info("ChargeDeposit 커맨드 생성 완료: userCode={}, amount={}",
			command.userCode(), command.amount());

		return command;
	}
}