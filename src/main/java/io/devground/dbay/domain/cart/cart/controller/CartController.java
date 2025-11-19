package io.devground.dbay.domain.cart.cart.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.domain.cart.cart.mapper.CartMapper;
import io.devground.dbay.domain.cart.cart.model.vo.AddCartItemRequest;
import io.devground.dbay.domain.cart.cart.model.vo.AddCartItemResponse;
import io.devground.dbay.domain.cart.cart.model.vo.DeleteItemsByCartRequest;
import io.devground.dbay.domain.cart.cart.model.vo.GetItemsByCartResponse;
import io.devground.dbay.domain.cart.cart.service.CartService;
import io.devground.dbay.domain.cart.cartItem.model.entity.CartItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
@Tag(name = "CartController")
public class CartController {

	private final CartService cartService;

	@PostMapping()
	@Operation(summary = "장바구니 상품 추가", description = "장바구니에 상품을 추가합니다.")
	public BaseResponse<AddCartItemResponse> addItem(
		@RequestHeader("X-CODE") String userCode,
		@RequestBody @Valid AddCartItemRequest request
	) {
		CartItem cartItem = cartService.addItem(userCode, request);
		return BaseResponse.success(200, CartMapper.toAddCartItemResponse(cartItem), "장바구니 추가 성공");
	}

	@GetMapping()
	@Operation(summary = "장바구니 상품 조회", description = "장바구니에 상품을 조회합니다.")
	public BaseResponse<GetItemsByCartResponse> getItemsByCart(@RequestHeader("X-CODE") String userCode) {
		return BaseResponse.success(200, cartService.getItemsByCart(userCode), "장바구니 조회 성공");
	}

	@DeleteMapping()
	@Operation(summary = "장바구니 상품 삭제", description = "장바구니 상품을 삭제합니다.(개별, 선택, 전체)")
	public BaseResponse<Integer> deleteItemsByCart(
		@RequestHeader("X-CODE") String userCode,
		@RequestBody @Valid DeleteItemsByCartRequest request) {
		return BaseResponse.success(200, cartService.deleteItemsByCart(userCode, request), "장바구니 상품 삭제 성공");
	}

}
