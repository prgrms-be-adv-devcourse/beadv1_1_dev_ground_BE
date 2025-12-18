package io.devground.core.events.settlement;

import java.util.List;

import lombok.Builder;

/**
 * 정산 생성 성공 이벤트
 * 배치 작업으로 Settlement가 생성되었을 때 발행
 * Order 도메인에서 이 이벤트를 받아서 주문 상태 업데이트
 */
@Builder
public record SettlementCreatedSuccess(
	List<String> orderCodes
) {
}