package io.devground.dbay.domain.order.order.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.event.order.OrderCreatedEvent;
import io.devground.core.model.entity.RoleType;
import io.devground.core.model.vo.DeleteStatus;
import io.devground.core.model.vo.ErrorCode;
import io.devground.core.model.web.PageDto;
import io.devground.core.util.Validators;
import io.devground.dbay.domain.order.infra.client.ProductFeignClient;
import io.devground.dbay.domain.order.order.mapper.OrderMapper;
import io.devground.dbay.domain.order.order.model.entity.Order;
import io.devground.dbay.domain.order.order.model.vo.CancelOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.ConfirmOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.CreateOrderRequest;
import io.devground.dbay.domain.order.order.model.vo.CreateOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.GetOrderDetailResponse;
import io.devground.dbay.domain.order.order.model.vo.GetOrdersResponse;
import io.devground.dbay.domain.order.order.model.vo.OrderItemInfo;
import io.devground.dbay.domain.order.order.model.vo.OrderProductListResponse;
import io.devground.dbay.domain.order.order.model.vo.PaidOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.UnsettledOrderItemResponse;
import io.devground.dbay.domain.order.order.repository.OrderRepository;
import io.devground.dbay.domain.order.orderItem.model.entity.OrderItem;
import io.devground.dbay.domain.order.orderItem.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	@Value("${orders.event.topic.order}")
	private String ordersEventTopicName;

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final ProductFeignClient productFeignClient;

	private final ApplicationEventPublisher eventPublisher;

	@Override
	@Transactional
	public CreateOrderResponse createOrder(String userCode, CreateOrderRequest request) {
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

		OrderCreatedEvent event = new OrderCreatedEvent(
			userCode,
			savedOrder.getCode(),
			totalAmount,
			items.stream().map(OrderItem::getProductCode).toList()
		);

		eventPublisher.publishEvent(event);

		return OrderMapper.toCreateOrderResponse(savedOrder);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<GetOrdersResponse> getOrders(String userCode, RoleType userRole, Pageable pageable) {
		if (!Validators.isValidUuid(userCode)) {
			throw ErrorCode.CODE_INVALID.throwServiceException();
		}

		Page<Order> orderPage = pageOrdersByRole(userCode, userRole, pageable);

		List<Order> orders = orderPage.getContent();

		if (orders.isEmpty()) {
			return Page.empty(pageable);
		}

		List<OrderItem> items = orderItemRepository.findByOrderInAndDeleteStatus(orders, DeleteStatus.N);

		Map<String, List<OrderItemInfo>> itemsByOrderId = items.stream()
			.collect(Collectors.groupingBy(
				oi -> oi.getOrder().getCode(),
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
				itemsByOrderId.getOrDefault(o.getCode(), Collections.emptyList())
			))
			.toList();

		return new PageImpl<>(content, pageable, orderPage.getTotalElements());
	}

	@Override
	@Transactional(readOnly = true)
	public GetOrderDetailResponse getOrderDetail(String userCode, String orderCode) {
		Order order = checkOrder(userCode, orderCode);

		List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

		long productTotalAmount = orderItems.stream()
			.mapToLong(OrderItem::getProductPrice)
			.sum();

		return new GetOrderDetailResponse(
			order.getCode(),
			order.getCreatedAt(),
			order.getOrderStatus(),
			order.getTotalAmount(),
			order.getTotalAmount() - productTotalAmount, // 할인금액
			productTotalAmount,
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
	@Transactional
	public ConfirmOrderResponse confirmOrder(String userCode, String orderCode) {
		Order order = checkOrder(userCode, orderCode);

		order.confirm();

		return OrderMapper.toConfirmOrderResponse(order);
	}

	@Override
	@Transactional(readOnly = true)
	public PageDto<UnsettledOrderItemResponse> getUnsettledOrderItems(int page, int size) {
		if (page < 0) {
			throw ErrorCode.PAGE_MUST_BE_POSITIVE.throwServiceException();
		}

		if (size < 0) {
			throw ErrorCode.PAGE_SIZE_MUST_BE_POSITIVE.throwServiceException();
		}

		LocalDate today = LocalDate.now().minusDays(1);
		LocalDate twoWeeksAgo = today.minusWeeks(2);

		LocalDateTime start = twoWeeksAgo.atStartOfDay();
		LocalDateTime end = today.atTime(LocalTime.MAX);

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

		Page<UnsettledOrderItemResponse> pageResult =
			orderItemRepository.findOrderItemsDelivered(start, end, pageable);

		return PageDto.from(pageResult);
	}

	@Override
	@Transactional
	public void confirmOrders(List<String> orderCodes) {
		orderRepository.DeleteByOrderCodes(orderCodes);
	}

	private Order checkOrder(String userCode, String orderCode) {
		if (!Validators.isValidUuid(userCode)) {
			throw ErrorCode.CODE_INVALID.throwServiceException();
		}

		if (!Validators.isValidUuid(orderCode)) {
			throw ErrorCode.CODE_INVALID.throwServiceException();
		}

		return orderRepository.findByCode(orderCode)
			.orElseThrow(ErrorCode.ORDER_NOT_FOUND::throwServiceException);

	}

	private Page<Order> pageOrdersByRole(String userCode, RoleType role, Pageable pageable) {
		return (role == RoleType.ADMIN)
			? orderRepository.findByDeleteStatusOrderByCreatedAtDesc(DeleteStatus.N, pageable)
			: orderRepository.findByUserCodeAndDeleteStatusOrderByCreatedAtDesc(userCode, DeleteStatus.N, pageable);
	}

}
