package io.devground.dbay.domain.order.order.model.vo;

import java.util.List;

import io.devground.dbay.domain.order.orderItem.model.entity.OrderItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

public record CreateOrderRequest(
	@NotBlank(message = "구매자명이 누락되었습니다.")
	String nickName,

	@NotBlank(message = "주소가 누락되었습니다.")
	String address,

	@NotEmpty(message = "주문할 상품을 선택해주세요.")
	List<String> cartProductCodes
) {
}
