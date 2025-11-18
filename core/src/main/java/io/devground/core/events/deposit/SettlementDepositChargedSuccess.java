package io.devground.core.events.deposit;

/**
 * 정산 예치금 충전 성공 이벤트
 * Settlement에서 판매자 예치금 입금이 완료되었을 때 발행
 */
public record SettlementDepositChargedSuccess(
	String userCode,
	String depositHistoryCode,
	Long amount,
	Long balanceAfter,
	String orderCode
) {
}