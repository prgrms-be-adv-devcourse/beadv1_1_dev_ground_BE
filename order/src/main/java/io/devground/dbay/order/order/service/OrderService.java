package io.devground.dbay.order.order.service;

import io.devground.core.model.entity.RoleType;
import io.devground.core.model.web.PageDto;
import io.devground.dbay.order.order.model.vo.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
	CreateOrderResponse createOrder(String userCode, CreateOrderRequest request);

	Page<GetOrdersResponse> getOrders(String userCode, RoleType userRole, Pageable pageable);

	GetOrderDetailResponse getOrderDetail(String userCode, String orderCode);

	CancelOrderResponse cancelOrder(String userCode, String orderCode);

	ConfirmOrderResponse confirmOrder(String userCode, String orderCode);

	PaidOrderResponse paidOrder(String userCode, String orderCode);

	PageDto<UnsettledOrderItemResponse> getUnsettledOrderItems(int page, int size);

	void confirmOrders(List<String> orderCodes);

	int autoUpdateOrderStatus();
}
