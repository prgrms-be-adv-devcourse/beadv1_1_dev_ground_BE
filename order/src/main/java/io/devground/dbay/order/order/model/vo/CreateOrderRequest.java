package io.devground.dbay.order.order.model.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
	@NotBlank(message = "구매자명이 누락되었습니다.")
	String nickName,

	@NotBlank(message = "주소가 누락되었습니다.")
	String address,

	@NotEmpty(message = "주문할 상품을 선택해주세요.")
	List<String> cartProductCodes
) {
}
