package io.devground.dbay.domain.order.order.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import io.devground.core.model.vo.DeleteStatus;
import io.devground.core.model.vo.ErrorCode;
import io.devground.core.util.Validators;
import io.devground.dbay.domain.order.infra.client.ProductFeignClient;
import io.devground.dbay.domain.order.infra.client.UserFeignClient;
import io.devground.dbay.domain.order.order.mapper.OrderMapper;
import io.devground.dbay.domain.order.order.model.entity.Order;
import io.devground.dbay.domain.order.order.model.vo.CancelOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.ConfirmOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.CreateOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.GetOrderDetailResponse;
import io.devground.dbay.domain.order.order.model.vo.GetOrdersResponse;
import io.devground.dbay.domain.order.order.model.vo.OrderItemInfo;
import io.devground.dbay.domain.order.order.model.vo.OrderProductListResponse;
import io.devground.dbay.domain.order.order.model.vo.CreateOrderRequest;
import io.devground.dbay.domain.order.order.model.vo.PaidOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.RoleType;
import io.devground.dbay.domain.order.order.repository.OrderRepository;
import io.devground.dbay.domain.order.orderItem.model.entity.OrderItem;
import io.devground.dbay.domain.order.orderItem.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final ProductFeignClient productFeignClient;
	private final UserFeignClient userFeignClient;



	@Override
	@Transactional
	public CreateOrderResponse createOrder(String userCode, CreateOrderRequest request) {
		// 코드 유효성 검증
		if (!Validators.isValidUuid(userCode)) {
			throw ErrorCode.CODE_INVALID.throwServiceException();
		}

		if (request.cartProductCodes() == null || request.cartProductCodes().isEmpty()) {
			throw ErrorCode.ORDER_ITEM_NOT_SELECTED.throwServiceException();
		}

		List<OrderProductListResponse> orderProducts = productFeignClient.productListByCodes(
			request.cartProductCodes());

		if (orderProducts.isEmpty() || orderProducts.size() != request.cartProductCodes().size()) {
			throw ErrorCode.ORDER_ITEM_ALREADY_SOLD.throwServiceException();
		}

		long totalAmount = orderProducts.stream()
			.mapToLong(OrderProductListResponse::price)
			.sum();

		// 쿠폰 기능 추가시 쿠폰 할인 여기서 처리

		Order order = Order.builder()
			.userCode(userCode)
			.nickName(request.nickName())
			.address(request.address())
			.totalAmount(totalAmount)
			.build();

		Order savedOrder = orderRepository.save(order);

		List<OrderItem> items = orderProducts.stream()
			.map(p -> OrderItem.builder()
				.order(savedOrder)
				.productCode(p.productCode())
				.sellerCode(p.sellerCode())
				.productName(p.title())
				.productPrice(p.price())
				.build()
			)
			.toList();

		orderItemRepository.saveAll(items);

		// payment kafka 이벤트 전송

		return OrderMapper.toCreateOrderResponse(savedOrder);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<GetOrdersResponse> getOrders(String userCode, Pageable pageable) {
		if (!Validators.isValidUuid(userCode)) {
			throw ErrorCode.CODE_INVALID.throwServiceException();
		}

		RoleType role = RoleType.USER; // userFeignClient.getUserRole(userCode);

		Page<Order> orderPage = pageOrdersByRole(userCode, role, pageable);

		List<Order> orders = orderPage.getContent();

		if (orders.isEmpty()) {
			return Page.empty(pageable);
		}

		List<OrderItem> items = orderItemRepository.findByOrderInAndDeleteStatus(orders, DeleteStatus.N);

		Map<Long, List<OrderItemInfo>> itemsByOrderId = items.stream()
			.collect(Collectors.groupingBy(
				oi -> oi.getOrder().getId(),
				Collectors.mapping(
					oi -> new OrderItemInfo(
						oi.getCode(),
						oi.getProductName(),
						oi.getProductPrice()
					),
					Collectors.toList()
				)
			));

		List<GetOrdersResponse> content = orders.stream()
			.map(o -> new GetOrdersResponse(
				o.getCode(),
				o.getCreatedAt(),
				o.getTotalAmount(),
				o.getOrderStatus(),
				itemsByOrderId.getOrDefault(o.getId(), Collections.emptyList())
			))
			.toList();

		return new PageImpl<>(content, pageable, orderPage.getTotalElements());
	}

	@Override
	@Transactional(readOnly = true)
	public GetOrderDetailResponse getOrderDetail(String userCode, String orderCode) {
		Order order = checkOrder(userCode, orderCode);

		long productTotalAmount = order.getOrderItems().stream()
			.mapToLong(OrderItem::getProductPrice)
			.sum();

		return new GetOrderDetailResponse(
			order.getCode(),
			order.getCreatedAt(),
			order.getOrderStatus(),
			order.getTotalAmount(),
			productTotalAmount,
			order.getTotalAmount() - productTotalAmount, // 할인금액
			0, // 배송비 일단 0원
			order.getNickName(),
			order.getAddress(),
			order.getOrderStatus().isCancellable()
		);
	}

	@Override
	@Transactional
	public PaidOrderResponse paidOrder(String userCode, String orderCode) {
		Order order = checkOrder(userCode, orderCode);

		order.paid();

		return OrderMapper.toPaidOrderResponse(order);
	}

	@Override
	@Transactional
	public CancelOrderResponse cancelOrder(String userCode, String orderCode) {
		Order order = checkOrder(userCode, orderCode);

		order.cancel();

		// 결제로 kafka 이벤트 전송

		return OrderMapper.toCancelOrderResponse(order);
	}

	@Override
	public ConfirmOrderResponse confirmOrder(String userCode, String orderCode) {
		Order order = checkOrder(userCode, orderCode);

		order.confirm();

		// 정산으로 kafka 이벤트 전송

		return OrderMapper.toConfirmOrderResponse(order);
	}

	private Order checkOrder(String userCode, String orderCode) {
		if (Validators.isValidUuid(userCode)) {
			throw ErrorCode.CODE_INVALID.throwServiceException();
		}

		if (Validators.isValidUuid(orderCode)) {
			throw ErrorCode.CODE_INVALID.throwServiceException();
		}

		RoleType role = userFeignClient.getUserRole(userCode);

		if (role == RoleType.USER) {
			return orderRepository.findByCodeAndUserCode(userCode, orderCode)
				.orElseThrow(ErrorCode.ORDER_NOT_FOUND::throwServiceException);
		} else {
			return orderRepository.findByCode(orderCode)
				.orElseThrow(ErrorCode.ORDER_NOT_FOUND::throwServiceException);
		}
	}

	private Page<Order> pageOrdersByRole(String userCode, RoleType role, Pageable pageable) {
		return (role == RoleType.ADMIN)
			? orderRepository.findByDeleteStatusOrderByCreatedAtDesc(DeleteStatus.N, pageable)
			: orderRepository.findByUserCodeAndDeleteStatusOrderByCreatedAtDesc(userCode, DeleteStatus.N, pageable);
	}

}
