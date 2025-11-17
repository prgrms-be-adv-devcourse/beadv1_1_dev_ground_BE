package io.devground.dbay.domain.settlement.batch.listener;

import org.springframework.batch.core.SkipListener;

import io.devground.dbay.domain.settlement.model.dto.UnsettledOrderItemResponse;
import io.devground.dbay.domain.settlement.model.entity.Settlement;
import lombok.extern.slf4j.Slf4j;

/**
 * 정산 배치 처리 중 Skip/Retry 발생 시 로깅 및 추적을 위한 리스너
 */
@Slf4j
public class SettlementStepListener implements SkipListener<UnsettledOrderItemResponse, Settlement> {

	/**
	 * Reader에서 Skip 발생 시
	 */
	@Override
	public void onSkipInRead(Throwable t) {
		log.error("정산 Reader에서 Skip 발생", t);
	}

	/**
	 * Processor에서 Skip 발생 시
	 */
	@Override
	public void onSkipInProcess(UnsettledOrderItemResponse item, Throwable t) {
		log.error("정산 Processor에서 Skip 발생: orderItemCode={}",
			item != null ? item.orderItemCode() : "null", t);

		// TODO: 실패한 항목을 별도 테이블에 저장하여 추후 재처리
	}

	/**
	 * Writer에서 Skip 발생 시
	 */
	@Override
	public void onSkipInWrite(Settlement item, Throwable t) {
		log.error("정산 Writer에서 Skip 발생: orderItemCode={}",
			item != null ? item.getOrderItemCode() : "null", t);

		// 정산 실패 상태로 변경
		if (item != null) {
			item.fail();
			log.warn("정산 실패 처리: orderItemCode={}", item.getOrderItemCode());
		}

		// TODO: 실패한 항목을 별도 테이블에 저장하여 추후 재처리
	}
}