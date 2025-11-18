package io.devground.dbay.domain.settlement.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 정산 생성 요청 DTO
 */
public record CreateSettlementRequest(
	@NotBlank(message = "주문 코드는 필수입니다")
	String orderCode,

	@NotBlank(message = "사용자 코드는 필수입니다")
	String userCode,

	@NotBlank(message = "주문 항목 코드는 필수입니다")
	String orderItemCode,

	@NotBlank(message = "판매자 코드는 필수입니다")
	String sellerCode,

	@NotNull(message = "상품 가격은 필수입니다")
	@Positive(message = "상품 가격은 0보다 커야 합니다")
	Long productPrice
) {
}