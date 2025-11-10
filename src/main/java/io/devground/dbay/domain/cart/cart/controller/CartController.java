package io.devground.dbay.domain.cart.cart.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.domain.cart.cart.mapper.CartMapper;
import io.devground.dbay.domain.cart.cart.model.vo.AddCartItemRequest;
import io.devground.dbay.domain.cart.cart.model.vo.AddCartItemResponse;
import io.devground.dbay.domain.cart.cart.service.CartService;
import io.devground.dbay.domain.cart.cartItem.model.entity.CartItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
@Tag(name = "CartController")
public class CartController {

	private final CartService cartService;

	@Operation(summary = "장바구니 상품 추가", description = "장바구니에 상품을 추가합니다.")
	@PostMapping("/{cartCode}")
	public BaseResponse<AddCartItemResponse> addItem(
		@PathVariable String cartCode,
		@RequestBody @Valid AddCartItemRequest request
	) {
		CartItem cartItem = cartService.addItem(cartCode, request);
		return BaseResponse.success(200, CartMapper.toAddCartItemResponse(cartItem), "장바구니 추가 성공");
	}
}
