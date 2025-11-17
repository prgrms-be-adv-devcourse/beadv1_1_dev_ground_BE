package io.devground.dbay.domain.order.order.model.vo;

import java.util.List;

public record UnsettledOrderItemResponse(
	String orderCode,
	String buyerCode,
	List<UnsettledOrderItemInfo> orderItems
) {
}
