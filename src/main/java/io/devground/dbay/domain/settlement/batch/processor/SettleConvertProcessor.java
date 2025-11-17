package io.devground.dbay.domain.settlement.batch.processor;

import java.time.LocalDateTime;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.devground.dbay.domain.settlement.model.dto.UnsettledOrderItemResponse;
import io.devground.dbay.domain.settlement.model.entity.Settlement;
import lombok.extern.slf4j.Slf4j;

/**
 * 정산되지 않은 OrderItem을 Settlement 엔티티로 변환하는 Processor
 */
@Slf4j
@StepScope
@Component
public class SettleConvertProcessor implements ItemProcessor<UnsettledOrderItemResponse, Settlement> {

	@Value("${custom.settlement.rate}")
	private Double rate;

	/**
	 * UnsettledOrderItemResponse를 Settlement 엔티티로 변환
	 * 정산 수수료와 정산 잔액을 자동 계산
	 */
	@Override
	public Settlement process(UnsettledOrderItemResponse item) throws Exception {
		log.info("Settlement 엔티티 생성 중: orderItemCode={}, rate={}", item.orderItemCode(), rate);

		Settlement settlement = Settlement.builder()
			.orderCode(item.orderCode())
			.orderItemCode(item.orderItemCode())
			.buyerCode(item.userCode())  // userCode가 buyerCode
			.sellerCode(item.sellerCode())
			.settlementDate(LocalDateTime.now())
			.settlementRate(rate)
			.totalAmount(item.productPrice())
			.build();

		// 정산 정책 적용 (수수료 및 정산 금액 계산)
		settlement.applySettlementPolicy();

		log.info("Settlement 엔티티 생성 완료: orderItemCode={}, totalAmount={}, rate={}",
			item.orderItemCode(), item.productPrice(), rate);

		return settlement;
	}
}
