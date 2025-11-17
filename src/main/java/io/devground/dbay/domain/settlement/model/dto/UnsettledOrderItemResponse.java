package io.devground.dbay.domain.settlement.model.dto;

/**
 * Order 도메인으로부터 정산되지 않은 OrderItem 정보를 받기 위한 DTO
 */
public record UnsettledOrderItemResponse(
	String orderCode,
	String userCode,
	String orderItemCode,
	String sellerCode,
	Long productPrice
) {
}