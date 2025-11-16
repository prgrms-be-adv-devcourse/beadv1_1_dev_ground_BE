package io.devground.dbay.domain.order.order.mapper;

import io.devground.dbay.domain.order.order.model.entity.Order;
import io.devground.dbay.domain.order.order.model.vo.CancelOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.ConfirmOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.CreateOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.PaidOrderResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderMapper {

	public CreateOrderResponse toCreateOrderResponse(Order order) {
		return new CreateOrderResponse(
			order.getUserCode(),
			order.getCode(),
			order.getNickName(),
			order.getAddress(),
			order.getTotalAmount()
		);
	}

	public CancelOrderResponse toCancelOrderResponse(Order order) {
		return new CancelOrderResponse(
			order.getCode(),
			order.getUpdatedAt()
		);
	}

	public ConfirmOrderResponse toConfirmOrderResponse(Order order) {
		return new ConfirmOrderResponse(
			order.getCode(),
			order.getUpdatedAt()
		);
	}

	public PaidOrderResponse toPaidOrderResponse(Order order) {
		return new PaidOrderResponse(
			order.getCode(),
			order.getUpdatedAt()
		);
	}
}
