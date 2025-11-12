package io.devground.dbay.domain.order.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;
import io.devground.core.model.web.PageDto;
import io.devground.dbay.domain.order.order.mapper.OrderMapper;
import io.devground.dbay.domain.order.order.model.entity.Order;
import io.devground.dbay.domain.order.order.model.vo.CancelOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.ConfirmOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.CreateOrderRequest;
import io.devground.dbay.domain.order.order.model.vo.CreateOrderResponse;
import io.devground.dbay.domain.order.order.model.vo.GetOrderDetailResponse;
import io.devground.dbay.domain.order.order.model.vo.GetOrdersResponse;
import io.devground.dbay.domain.order.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Tag(name = "OrderController")
public class OrderController {

	private final OrderService orderService;

	@PostMapping("/{userCode}")
	@Operation(summary = "주문 생성", description = "상품 주문")
	public BaseResponse<CreateOrderResponse> createOrder(
		@PathVariable String userCode,
		@RequestBody @Valid CreateOrderRequest request
	) {
		return BaseResponse.success(200, orderService.createOrder(userCode, request), "주문 생성이 완료되었습니다.");
	}

	@GetMapping
	@Operation(summary = "주문 조회", description = "주문 목록 조회")
	public BaseResponse<Page<GetOrdersResponse>> getOrders(
		@RequestHeader("X-CODE") String userCode,
		@PageableDefault Pageable pageable
	) {
		return BaseResponse.success(200, orderService.getOrders(userCode, pageable), "주문 목록 조회 성공");
	}

	@GetMapping("/{orderCode}")
	@Operation(summary = "주문 상세 조회", description = "주문 상세 조회")
	public BaseResponse<GetOrderDetailResponse> getOrderDetail(
		@RequestHeader("X-CODE") String userCode,
		@PathVariable String orderCode
	) {
		return BaseResponse.success(200, orderService.getOrderDetail(userCode, orderCode), "주문 상세 조회 성공");
	}

	@PatchMapping("/{orderCode}")
	@Operation(summary = "주문 취소", description = "주문 취소")
	public BaseResponse<CancelOrderResponse> cancelOrder(
		@RequestHeader("X-CODE") String userCode,
		@PathVariable String orderCode
	) {
		return BaseResponse.success(200, orderService.cancelOrder(userCode, orderCode), "주문 삭제 성공");
	}

	@PatchMapping("/confirm/{orderCode}")
	@Operation(summary = "주문 구매 확정", description = "주문 구매 확정")
	public BaseResponse<ConfirmOrderResponse> confirmOrder(
		@RequestHeader("X-CODE") String userCode,
		@PathVariable String orderCode
	) {
		return BaseResponse.success(200, orderService.confirmOrder(userCode, orderCode), "구매 확정 완료");
	}
}
