package io.devground.dbay.domain.order.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.devground.core.model.entity.RoleType;
import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.DeleteStatus;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.order.infra.client.ProductFeignClient;
import io.devground.dbay.domain.order.order.model.entity.Order;
import io.devground.dbay.domain.order.order.model.vo.CreateOrderRequest;
import io.devground.dbay.domain.order.order.model.vo.CreateOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.GetOrderDetailResponse;
import io.devground.dbay.domain.order.order.model.vo.GetOrdersResponse;
import io.devground.dbay.domain.order.order.model.vo.OrderItemInfo;
import io.devground.dbay.domain.order.order.model.vo.OrderProductListResponse;
import io.devground.dbay.domain.order.order.model.vo.OrderStatus;
import io.devground.dbay.domain.order.order.repository.OrderRepository;
import io.devground.dbay.domain.order.order.service.OrderServiceImpl;
import io.devground.dbay.domain.order.orderItem.model.entity.OrderItem;
import io.devground.dbay.domain.order.orderItem.repository.OrderItemRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private OrderItemRepository orderItemRepository;

	@Mock
	private ProductFeignClient productFeignClient;

	@InjectMocks
	@Spy
	private OrderServiceImpl orderService;

	@Test
	@DisplayName("성공_주문 생성")
	void createOrder_success() {
		String userCode = UUID.randomUUID().toString();

		String productCode1 = UUID.randomUUID().toString();
		String productCode2 = UUID.randomUUID().toString();

		String productSaleCode1 = UUID.randomUUID().toString();
		String productSaleCode2 = UUID.randomUUID().toString();

		String sellerCode1 = UUID.randomUUID().toString();
		String sellerCode2 = UUID.randomUUID().toString();

		CreateOrderRequest request = new CreateOrderRequest(
			"테스트",
			"서울",
			List.of(productCode1, productCode2)
		);

		OrderProductListResponse response1 = new OrderProductListResponse(
			productCode1,
			productSaleCode1,
			sellerCode1,
			"아이폰 17",
			1500000L
		);

		OrderProductListResponse response2 = new OrderProductListResponse(
			productCode2,
			productSaleCode2,
			sellerCode2,
			"맥북",
			2500000L
		);

		given(productFeignClient.productListByCodes(request.cartProductCodes()))
			.willReturn(List.of(response1, response2));

		long totalAmount = 4000000L;

		given(orderRepository.save(any(Order.class)))
			.willAnswer(invocation -> invocation.getArgument(0));

		given(orderItemRepository.saveAll(anyList()))
			.willAnswer(invocation -> invocation.getArgument(0));

		CreateOrderResponse result = orderService.createOrder(userCode, request);

		assertThat(result.totalAmount()).isEqualTo(totalAmount);
		assertThat(result.nickName()).isEqualTo("테스트");
		assertThat(result.address()).isEqualTo("서울");

		verify(productFeignClient).productListByCodes(request.cartProductCodes());
		verify(orderRepository).save(any(Order.class));
		verify(orderItemRepository).saveAll(anyList());
	}

	@Test
	@DisplayName("실패_주문 생성 유저 코드 유효값 아님")
	void createOrder_thrownException_whenInvalidCode() {
		String userCode = "Invalid Code";

		CreateOrderRequest request = mock(CreateOrderRequest.class);

		assertThatThrownBy(() -> orderService.createOrder(userCode, request))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
			});

		verifyNoInteractions(productFeignClient, orderRepository, orderItemRepository);
	}

	@Test
	@DisplayName("실패_주문 생성 장바구니 선택 상품이 없음(null)")
	void createOrder_thrownException_whenCartProductsNull() {
		String userCode = UUID.randomUUID().toString();

		CreateOrderRequest request = mock(CreateOrderRequest.class);

		assertThatThrownBy(() -> orderService.createOrder(userCode, request))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.ORDER_ITEM_NOT_SELECTED);
			});

		verifyNoInteractions(productFeignClient, orderRepository, orderItemRepository);
	}

	@Test
	@DisplayName("실패_주문 생성 장바구니 선택 상품이 없음(empty)")
	void createOrder_thrownException_whenCartProductsEmpty() {
		String userCode = UUID.randomUUID().toString();

		CreateOrderRequest request = new CreateOrderRequest(
			"테스트",
			"서울",
			List.of()
		);

		assertThatThrownBy(() -> orderService.createOrder(userCode, request))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.ORDER_ITEM_NOT_SELECTED);
			});

		verifyNoInteractions(productFeignClient, orderRepository, orderItemRepository);
	}

	@Test
	@DisplayName("실패_주문 생성 장바구니 상품 중에 하나가 이미 판매")
	void createOrder_thrownException_whenCartProductAlreadySold() {
		String userCode = UUID.randomUUID().toString();

		String productCode1 = UUID.randomUUID().toString();
		String productCode2 = UUID.randomUUID().toString();

		CreateOrderRequest request = new CreateOrderRequest(
			"테스트",
			"서울",
			List.of(productCode1, productCode2)
		);

		OrderProductListResponse response = mock(OrderProductListResponse.class);

		given(productFeignClient.productListByCodes(request.cartProductCodes()))
			.willReturn(List.of(response));

		assertThatThrownBy(() -> orderService.createOrder(userCode, request))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.ORDER_ITEM_ALREADY_SOLD);
			});

		verifyNoInteractions(orderRepository, orderItemRepository);
	}

	@Test
	@DisplayName("성공_주문 조회(사용자)")
	void getOrders_success_user() {
		String userCode = UUID.randomUUID().toString();
		RoleType userRole = RoleType.USER;
		Pageable pageable = PageRequest.of(0, 10);
		String sellerCode1 = UUID.randomUUID().toString();
		String sellerCode2 = UUID.randomUUID().toString();

		Order order = Order.builder()
			.userCode(userCode)
			.nickName("테스트")
			.address("서울")
			.totalAmount(3000000L)
			.build();

		Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);

		given(orderRepository.findByUserCodeAndDeleteStatusOrderByCreatedAtDesc(userCode, DeleteStatus.N, pageable))
			.willReturn(orderPage);

		OrderItem item1 = OrderItem.builder()
			.order(order)
			.productCode(UUID.randomUUID().toString())
			.sellerCode(sellerCode1)
			.productName("아이폰 17")
			.productPrice(1000000L)
			.build();

		OrderItem item2 = OrderItem.builder()
			.order(order)
			.productCode(UUID.randomUUID().toString())
			.sellerCode(sellerCode2)
			.productName("맥북")
			.productPrice(2000000L)
			.build();

		given(orderItemRepository.findByOrderInAndDeleteStatus(List.of(order), DeleteStatus.N))
			.willReturn(List.of(item1, item2));

		Page<GetOrdersResponse> result = orderService.getOrders(userCode, userRole, pageable);

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).hasSize(1);

		GetOrdersResponse orderResponse = result.getContent().getFirst();

		assertThat(orderResponse.totalAmount()).isEqualTo(3000000L);
		assertThat(orderResponse.items()).hasSize(2);

		assertThat(orderResponse.items())
			.extracting(OrderItemInfo::productName)
			.containsExactlyInAnyOrder("아이폰 17", "맥북");

		assertThat(orderResponse.items())
			.extracting(OrderItemInfo::productPrice)
			.containsExactlyInAnyOrder(1000000L, 2000000L);

		verify(orderRepository).findByUserCodeAndDeleteStatusOrderByCreatedAtDesc(userCode, DeleteStatus.N, pageable);
		verify(orderItemRepository).findByOrderInAndDeleteStatus(List.of(order), DeleteStatus.N);
	}

	@Test
	@DisplayName("성공_주문 조회(관리자)")
	void getOrders_success_admin() {
		String userCode = UUID.randomUUID().toString();
		String userCode2 = UUID.randomUUID().toString();
		RoleType userRole = RoleType.ADMIN;
		Pageable pageable = PageRequest.of(0, 10);
		String sellerCode1 = UUID.randomUUID().toString();
		String sellerCode2 = UUID.randomUUID().toString();

		Order order = Order.builder()
			.userCode(userCode)
			.nickName("테스트")
			.address("서울")
			.totalAmount(1000000L)
			.build();

		Order order2 = Order.builder()
			.userCode(userCode2)
			.nickName("테스트2")
			.address("서울")
			.totalAmount(2000000L)
			.build();

		Page<Order> orderPage = new PageImpl<>(List.of(order, order2), pageable, 1);

		given(orderRepository
			.findByDeleteStatusOrderByCreatedAtDesc(DeleteStatus.N, pageable))
			.willReturn(orderPage);

		OrderItem item1 = OrderItem.builder()
			.order(order)
			.productCode(UUID.randomUUID().toString())
			.sellerCode(sellerCode1)
			.productName("아이폰 17")
			.productPrice(1000000L)
			.build();

		OrderItem item2 = OrderItem.builder()
			.order(order2)
			.productCode(UUID.randomUUID().toString())
			.sellerCode(sellerCode2)
			.productName("맥북")
			.productPrice(2000000L)
			.build();

		given(orderItemRepository.findByOrderInAndDeleteStatus(List.of(order, order2), DeleteStatus.N))
			.willReturn(List.of(item1, item2));

		Page<GetOrdersResponse> result = orderService.getOrders(userCode, userRole, pageable);

		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).hasSize(2);

		GetOrdersResponse orderResponse = result.getContent().get(0);
		GetOrdersResponse orderResponse2 = result.getContent().get(1);

		assertThat(orderResponse.totalAmount()).isEqualTo(1000000L);
		assertThat(orderResponse2.totalAmount()).isEqualTo(2000000L);
		assertThat(orderResponse.items()).hasSize(1);
		assertThat(orderResponse2.items()).hasSize(1);
		assertThat(orderResponse.items().getFirst().productName()).isEqualTo("아이폰 17");
		assertThat(orderResponse2.items().getFirst().productName()).isEqualTo("맥북");

		verify(orderRepository).findByDeleteStatusOrderByCreatedAtDesc(DeleteStatus.N, pageable);
		verify(orderItemRepository).findByOrderInAndDeleteStatus(List.of(order, order2), DeleteStatus.N);
	}

	@Test
	@DisplayName("성공_빈 주문 조회")
	void getOrders_success_empty() {
		String userCode = UUID.randomUUID().toString();
		RoleType userRole = RoleType.USER;
		Pageable pageable = PageRequest.of(0, 10);

		Page<Order> emptyOrderPage = Page.empty(pageable);

		given(orderRepository.findByUserCodeAndDeleteStatusOrderByCreatedAtDesc(
			userCode,
			DeleteStatus.N,
			pageable
		)).willReturn(emptyOrderPage);

		Page<GetOrdersResponse> result = orderService.getOrders(userCode, userRole, pageable);

		assertThat(result).isEmpty();

		verify(orderRepository).findByUserCodeAndDeleteStatusOrderByCreatedAtDesc(
			userCode,
			DeleteStatus.N,
			pageable
		);

		verifyNoInteractions(orderItemRepository);
	}

	@Test
	@DisplayName("실패_주문 조회 유저 코드 유효하지않음")
	void getOrders_thrownException_InvalidCode() {
		Pageable pageable = mock(Pageable.class);

		assertThatThrownBy(() -> orderService.getOrders("Invalid Code", RoleType.USER, pageable))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
			});

		verifyNoInteractions(orderRepository, orderItemRepository);
	}

	@Test
	@DisplayName("성공_주문 상세 조회")
	void getOrderDetail_success_user() {
		String userCode = UUID.randomUUID().toString();
		String productCode1 = UUID.randomUUID().toString();
		String productCode2 = UUID.randomUUID().toString();
		String sellerCode1 = UUID.randomUUID().toString();
		String sellerCode2 = UUID.randomUUID().toString();

		Order order = Order.builder()
			.userCode(userCode)
			.nickName("테스트")
			.address("서울")
			.totalAmount(3000L)
			.build();

		OrderItem item1 = OrderItem.builder()
			.order(order)
			.productCode(productCode1)
			.sellerCode(sellerCode1)
			.productName("아이폰")
			.productPrice(1000L)
			.build();

		OrderItem item2 = OrderItem.builder()
			.order(order)
			.productCode(productCode2)
			.sellerCode(sellerCode2)
			.productName("맥북")
			.productPrice(2000L)
			.build();

		given(orderRepository.findByCode(order.getCode()))
			.willReturn(Optional.of(order));

		given(orderItemRepository.findByOrder(order))
			.willReturn(List.of(item1, item2));

		long productTotalAmount = Stream.of(item1, item2)
			.mapToLong(OrderItem::getProductPrice)
			.sum();

		log.info("코드: {}", order.getCode());
		log.info("가격: {}", productTotalAmount);

		GetOrderDetailResponse response = orderService.getOrderDetail(userCode, order.getCode());

		assertThat(response.orderCode()).isEqualTo(order.getCode());
		assertThat(response.orderStatus()).isEqualTo(OrderStatus.PENDING);
		assertThat(response.totalAmount()).isEqualTo(3000L);
		assertThat(response.productTotalAmount()).isEqualTo(3000L);
		assertThat(response.discount()).isEqualTo(0L);
		assertThat(response.shippingFee()).isEqualTo(0);
		assertThat(response.cancellable()).isTrue();
	}

	@Test
	@DisplayName("실패_주문 상세 조회 유저 코드 유효한값 아님")
	void getOrderDetail_thrownException_InvalidUserCode() {
		String userCode = "Invalid Code";

		assertThatThrownBy(() -> orderService.getOrderDetail(userCode, UUID.randomUUID().toString()))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
			});

		verifyNoInteractions(orderRepository, orderItemRepository);
	}

	@Test
	@DisplayName("실패_주문 상세 조회 주문 코드 유효한값 아님")
	void getOrderDetail_thrownException_InvalidOrderCode() {
		String orderCode = "Invalid Code";

		assertThatThrownBy(() -> orderService.getOrderDetail(UUID.randomUUID().toString(), orderCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
			});

		verifyNoInteractions(orderRepository, orderItemRepository);
	}

	@Test
	@DisplayName("실패_주문 상세 주문이 존재하지 않을 때")
	void getOrderDetail_thrownException_whenOrderNotExists() {
		String userCode = UUID.randomUUID().toString();
		String orderCode = UUID.randomUUID().toString();

		given(orderRepository.findByCode(orderCode)).willReturn(Optional.empty());

		assertThatThrownBy(() -> orderService.getOrderDetail(userCode, orderCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.ORDER_NOT_FOUND);
			});

		verifyNoInteractions(orderItemRepository);
	}

	@Test
	@DisplayName("성공_주문 상태 취소")
	void cancelOrder_success() {
		String userCode = UUID.randomUUID().toString();
		String orderCode = UUID.randomUUID().toString();

		Order order = Order.builder()
			.userCode(userCode)
			.nickName("테스트")
			.address("서울")
			.totalAmount(1000L)
			.build();

		given(orderRepository.findByCode(orderCode)).willReturn(Optional.of(order));

		orderService.cancelOrder(userCode, orderCode);

		assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
	}

	@Test
	@DisplayName("실패_주문 상태 취소 유저 코드 유효값 아님")
	void cancelOrder_thrownException_whenInvalidUserCode() {
		String userCode = "Invalid Code";

		assertThatThrownBy(() -> orderService.cancelOrder(userCode, UUID.randomUUID().toString()))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
			});

		verifyNoInteractions(orderRepository);
	}

	@Test
	@DisplayName("실패_주문 상태 취소 주문 코드 유효값 아님")
	void cancelOrder_thrownException_whenInvalidOrderCode() {
		String orderCode = "Invalid Code";

		assertThatThrownBy(() -> orderService.cancelOrder(UUID.randomUUID().toString(), orderCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
			});

		verifyNoInteractions(orderRepository);
	}

	@Test
	@DisplayName("실패_주문 상태 취소 존재하지 않는 주문")
	void cancelOrder_thrownException_whenOrderNotExists() {
		String userCode = UUID.randomUUID().toString();
		String orderCode = UUID.randomUUID().toString();

		given(orderRepository.findByCode(orderCode)).willReturn(Optional.empty());

		assertThatThrownBy(() -> orderService.cancelOrder(userCode, orderCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.ORDER_NOT_FOUND);
			});
	}

	@Test
	@DisplayName("실패_주문 상태 배송완료일때 취소시")
	void cancelOrder_thrownException_whenDelivered() {
		String userCode = UUID.randomUUID().toString();
		String orderCode = UUID.randomUUID().toString();

		Order order = Order.builder()
			.userCode(userCode)
			.nickName("테스트")
			.address("서울")
			.totalAmount(1000L)
			.build();

		order.setOrderStatus(OrderStatus.DELIVERED);

		given(orderRepository.findByCode(orderCode)).willReturn(Optional.of(order));

		assertThatThrownBy(() -> orderService.cancelOrder(userCode, orderCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.ORDER_CANCELLED_NOT_ALLOWED_WHEN_DELIVERED);
			});
	}

	@Test
	@DisplayName("실패_주문 상태 취소일때 취소시")
	void cancelOrder_thrownException_whenAlreadyCancel() {
		String userCode = UUID.randomUUID().toString();
		String orderCode = UUID.randomUUID().toString();

		Order order = Order.builder()
			.userCode(userCode)
			.nickName("테스트")
			.address("서울")
			.totalAmount(1000L)
			.build();

		order.setOrderStatus(OrderStatus.CANCELLED);

		given(orderRepository.findByCode(orderCode)).willReturn(Optional.of(order));

		assertThatThrownBy(() -> orderService.cancelOrder(userCode, orderCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.ORDER_ALREADY_CANCELLED);
			});
	}

	@Test
	@DisplayName("실패_주문 상태 확정일때 취소시")
	void cancelOrder_thrownException_whenConfirmed() {
		String userCode = UUID.randomUUID().toString();
		String orderCode = UUID.randomUUID().toString();

		Order order = Order.builder()
			.userCode(userCode)
			.nickName("테스트")
			.address("서울")
			.totalAmount(1000L)
			.build();

		order.setOrderStatus(OrderStatus.CONFIRMED);

		given(orderRepository.findByCode(orderCode)).willReturn(Optional.of(order));

		assertThatThrownBy(() -> orderService.cancelOrder(userCode, orderCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.ORDER_CANCELLED_NOT_ALLOWED_WHEN_CONFIRMED);
			});
	}

	@Test
	@DisplayName("성공_주문 상태 확정")
	void confirmOrder_success() {
		String userCode = UUID.randomUUID().toString();
		String orderCode = UUID.randomUUID().toString();

		Order order = Order.builder()
			.userCode(userCode)
			.nickName("테스트")
			.address("서울")
			.totalAmount(1000L)
			.build();

		order.setOrderStatus(OrderStatus.DELIVERED);

		given(orderRepository.findByCode(orderCode)).willReturn(Optional.of(order));

		orderService.confirmOrder(userCode, orderCode);

		assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED);
	}

	@Test
	@DisplayName("실패_주문 상태 확정 유저 코드 유효값 아님")
	void confirmOrder_thrownException_whenInvalidUserCode() {
		String userCode = "Invalid Code";

		assertThatThrownBy(() -> orderService.confirmOrder(userCode, UUID.randomUUID().toString()))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
			});

		verifyNoInteractions(orderRepository);
	}

	@Test
	@DisplayName("실패_주문 상태 확정 주문 코드 유효값 아님")
	void confirmOrder_thrownException_whenInvalidOrderCode() {
		String orderCode = "Invalid Code";

		assertThatThrownBy(() -> orderService.confirmOrder(UUID.randomUUID().toString(), orderCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
			});

		verifyNoInteractions(orderRepository);
	}

	@Test
	@DisplayName("실패_주문 상태 확정 존재하지 않는 주문")
	void confirmOrder_thrownException_whenOrderNotExists() {
		String userCode = UUID.randomUUID().toString();
		String orderCode = UUID.randomUUID().toString();

		given(orderRepository.findByCode(orderCode)).willReturn(Optional.empty());

		assertThatThrownBy(() -> orderService.confirmOrder(userCode, orderCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.ORDER_NOT_FOUND);
			});
	}

	@Test
	@DisplayName("실패_주문 상태 확정일때 확정시")
	void confirmOrder_thrownException_whenAlreadyConfirmed() {
		String userCode = UUID.randomUUID().toString();
		String orderCode = UUID.randomUUID().toString();

		Order order = Order.builder()
			.userCode(userCode)
			.nickName("테스트")
			.address("서울")
			.totalAmount(1000L)
			.build();

		order.setOrderStatus(OrderStatus.CONFIRMED);

		given(orderRepository.findByCode(orderCode)).willReturn(Optional.of(order));

		assertThatThrownBy(() -> orderService.confirmOrder(userCode, orderCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.ORDER_ALREADY_CONFIRMED);
			});
	}

	@Test
	@DisplayName("실패_주문 상태 보류일때 확정시")
	void confirmOrder_thrownException_whenPending() {
		String userCode = UUID.randomUUID().toString();
		String orderCode = UUID.randomUUID().toString();

		Order order = Order.builder()
			.userCode(userCode)
			.nickName("테스트")
			.address("서울")
			.totalAmount(1000L)
			.build();

		given(orderRepository.findByCode(orderCode)).willReturn(Optional.of(order));

		assertThatThrownBy(() -> orderService.confirmOrder(userCode, orderCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.ORDER_CONFIRM_NOT_ALLOWED_BEFORE_DELIVERED);
			});
	}

	@Test
	@DisplayName("실패_주문 상태 취소일때 확정시")
	void confirmOrder_thrownException_whenCanceled() {
		String userCode = UUID.randomUUID().toString();
		String orderCode = UUID.randomUUID().toString();

		Order order = Order.builder()
			.userCode(userCode)
			.nickName("테스트")
			.address("서울")
			.totalAmount(1000L)
			.build();

		order.setOrderStatus(OrderStatus.CANCELLED);

		given(orderRepository.findByCode(orderCode)).willReturn(Optional.of(order));

		assertThatThrownBy(() -> orderService.confirmOrder(userCode, orderCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.ORDER_CONFIRM_NOT_ALLOWED_BEFORE_DELIVERED);
			});
	}

	@Test
	@DisplayName("실패_주문 상태 구매일때 확정시")
	void confirmOrder_thrownException_whenPaid() {
		String userCode = UUID.randomUUID().toString();
		String orderCode = UUID.randomUUID().toString();

		Order order = Order.builder()
			.userCode(userCode)
			.nickName("테스트")
			.address("서울")
			.totalAmount(1000L)
			.build();

		order.setOrderStatus(OrderStatus.PAID);

		given(orderRepository.findByCode(orderCode)).willReturn(Optional.of(order));

		assertThatThrownBy(() -> orderService.confirmOrder(userCode, orderCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.ORDER_CONFIRM_NOT_ALLOWED_BEFORE_DELIVERED);
			});
	}

	@Test
	@DisplayName("실패_주문 상태 배송중일때 확정시")
	void confirmOrder_thrownException_whenDelivery() {
		String userCode = UUID.randomUUID().toString();
		String orderCode = UUID.randomUUID().toString();

		Order order = Order.builder()
			.userCode(userCode)
			.nickName("테스트")
			.address("서울")
			.totalAmount(1000L)
			.build();

		order.setOrderStatus(OrderStatus.START_DELIVERY);

		given(orderRepository.findByCode(orderCode)).willReturn(Optional.of(order));

		assertThatThrownBy(() -> orderService.confirmOrder(userCode, orderCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.ORDER_CONFIRM_NOT_ALLOWED_BEFORE_DELIVERED);
			});
	}

}
