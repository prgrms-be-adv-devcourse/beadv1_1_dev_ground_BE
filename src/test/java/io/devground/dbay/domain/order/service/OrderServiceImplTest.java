package io.devground.dbay.domain.order.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.devground.dbay.domain.order.infra.client.ProductFeignClient;
import io.devground.dbay.domain.order.order.model.entity.Order;
import io.devground.dbay.domain.order.order.model.vo.CreateOrderRequest;
import io.devground.dbay.domain.order.order.model.vo.CreateOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.OrderProductListResponse;
import io.devground.dbay.domain.order.order.repository.OrderRepository;
import io.devground.dbay.domain.order.order.service.OrderServiceImpl;
import io.devground.dbay.domain.order.orderItem.model.entity.OrderItem;
import io.devground.dbay.domain.order.orderItem.repository.OrderItemRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private OrderItemRepository orderItemRepository;

	@Mock
	private ProductFeignClient productFeignClient;

	@InjectMocks
	private OrderServiceImpl orderService;

	private String userCode;
	private CreateOrderRequest request;

	@BeforeEach
	void setUp() {
		userCode = UUID.randomUUID().toString();

		request = new CreateOrderRequest(
			"테스트",
			"서울시",
			List.of("P-001", "P-002")
		);
	}

	private List<OrderProductListResponse> givenProducts() {
		return List.of(
			new OrderProductListResponse("P-001", "S-111", "seller-1", "아이폰", 500000L),
			new OrderProductListResponse("P-002", "S-222", "seller-2", "아이패드", 300000L)
		);
	}

	@Test
	@DisplayName("성공_주문 생성")
	void createOrder_success() {
		List<OrderProductListResponse> products = givenProducts();
		given(productFeignClient.productListByCodes(request.cartProductCodes())).willReturn(products);

		// @SuppressWarnings("unchecked")
		// ArgumentCaptor<List<OrderItem>> itemsCaptor = ArgumentCaptor.forClass(List.class);
		//
		// CreateOrderResponse response = orderService.createOrder(userCode, request);
		//
		// verify(orderRepository, times(1)).save(any(Order.class));
		// verify(orderItemRepository, times(1)).saveAll(itemsCaptor.capture());



	}
}
