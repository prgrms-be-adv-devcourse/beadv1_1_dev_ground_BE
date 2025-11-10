package io.devground.dbay.domain.cart.cart.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.vo.ErrorCode;
import io.devground.core.util.Validators;
import io.devground.dbay.domain.cart.cart.model.entity.Cart;
import io.devground.dbay.domain.cart.cart.model.vo.AddCartItemRequest;
import io.devground.dbay.domain.cart.cart.repository.CartRepository;
import io.devground.dbay.domain.cart.cartItem.model.entity.CartItem;
import io.devground.dbay.domain.cart.cartItem.repository.CartItemRepository;
import io.devground.dbay.domain.cart.infra.client.ProductFeignClient;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductFeignClient productFeignClient;

	@Override
	@Transactional
	public Cart createCart(String userCode) {

		if (!Validators.isValidUuid(userCode)) {
			throw ErrorCode.CODE_INVALID.throwServiceException();
		}

		if (cartRepository.existsByUserCode(userCode)) {
			throw ErrorCode.CART_ALREADY_EXIST.throwServiceException();
		}

		Cart cart = Cart.builder()
			.userCode(userCode)
			.build();

		// deposit에 kafka 이벤트 전달

		return cartRepository.save(cart);
	}

	@Override
	@Transactional
	public CartItem addItem(String cartCode, AddCartItemRequest request) {

		if (!Validators.isValidUuid(cartCode) || !Validators.isValidUuid(request.productCode())) {
			throw ErrorCode.CODE_INVALID.throwServiceException();
		}

		if (!cartRepository.existsByCode(cartCode)) {
			throw ErrorCode.CART_NOT_FOUND.throwServiceException();
		}

		if (cartItemRepository.existsByCart_CodeAndProductCode(cartCode, request.productCode())) {
			throw ErrorCode.CART_ITEM_ALREADY_EXIST.throwServiceException();
		}

		// 상품 상세 API 구현 후 주석 해제
		// if (!Objects.equals(productFeignClient.productInfoByCode(request.productCode()).productStatus(), "ON_SALE")) {
		// 	throw ErrorCode.SOLD_PRODUCT_CANNOT_PURCHASE.throwServiceException();
		// }

		Cart cart = cartRepository.findByCode(cartCode);

		CartItem cartItem = CartItem.builder()
			.cart(cart)
			.productCode(request.productCode())
			.build();

		return cartItemRepository.save(cartItem);
	}
}
