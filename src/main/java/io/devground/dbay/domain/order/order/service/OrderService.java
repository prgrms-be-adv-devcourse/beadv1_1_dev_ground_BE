package io.devground.dbay.domain.order.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.devground.dbay.domain.order.order.model.entity.Order;
import io.devground.dbay.domain.order.order.model.vo.CancelOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.ConfirmOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.CreateOrderRequest;
import io.devground.dbay.domain.order.order.model.vo.CreateOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.GetOrderDetailResponse;
import io.devground.dbay.domain.order.order.model.vo.GetOrdersResponse;
import io.devground.dbay.domain.order.order.model.vo.PaidOrderResponse;

public interface OrderService {
	CreateOrderResponse createOrder(String userCode, CreateOrderRequest request);

	Page<GetOrdersResponse> getOrders(String userCode, Pageable pageable);

	GetOrderDetailResponse getOrderDetail(String userCode, String orderCode);

	CancelOrderResponse cancelOrder(String userCode, String orderCode);

	ConfirmOrderResponse confirmOrder(String userCode, String orderCode);

	PaidOrderResponse paidOrder(String userCode, String orderCode);
}
