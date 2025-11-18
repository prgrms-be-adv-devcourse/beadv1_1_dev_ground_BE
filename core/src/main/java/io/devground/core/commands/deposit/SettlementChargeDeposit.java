package io.devground.core.commands.deposit;

/**
 * 정산을 위한 예치금 충전 커맨드
 * Settlement에서 판매자에게 정산금을 입금할 때 사용
 */
public record SettlementChargeDeposit(
	String userCode,
	Long amount,
	String orderCode
) {
}