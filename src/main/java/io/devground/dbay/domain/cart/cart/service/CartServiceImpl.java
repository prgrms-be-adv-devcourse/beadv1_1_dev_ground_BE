package io.devground.dbay.domain.cart.cart.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.vo.ErrorCode;
import io.devground.core.util.Validators;
import io.devground.dbay.domain.cart.cart.model.entity.Cart;
import io.devground.dbay.domain.cart.cart.model.vo.AddCartItemRequest;
import io.devground.dbay.domain.cart.cart.model.vo.CartProductListResponse;
import io.devground.dbay.domain.cart.cart.model.vo.DeleteItemsByCartRequest;
import io.devground.dbay.domain.cart.cart.model.vo.GetItemsByCartResponse;
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

		return cartRepository.save(cart);
	}

	@Override
	@Transactional
	public CartItem addItem(String cartCode, AddCartItemRequest request) {

		if (!Validators.isValidUuid(cartCode) || !Validators.isValidUuid(request.productCode())) {
			throw ErrorCode.CODE_INVALID.throwServiceException();
		}

		if (cartItemRepository.existsByCart_CodeAndProductCode(cartCode, request.productCode())) {
			throw ErrorCode.CART_ITEM_ALREADY_EXIST.throwServiceException();
		}

		// 상품 상세 API 구현 후 주석 해제
		// if (!Objects.equals(productFeignClient.productInfoByCode(request.productCode()).productStatus(), "ON_SALE")) {
		// 	throw ErrorCode.SOLD_PRODUCT_CANNOT_PURCHASE.throwServiceException();
		// }

		Cart cart = cartRepository.findByCode(cartCode).orElseThrow(ErrorCode.CART_NOT_FOUND::throwServiceException);

		CartItem cartItem = CartItem.builder()
			.cart(cart)
			.productCode(request.productCode())
			.build();

		return cartItemRepository.save(cartItem);
	}

	@Override
	@Transactional(readOnly = true)
	public GetItemsByCartResponse getItemsByCart(String cartCode) {
		if (!Validators.isValidUuid(cartCode)) {
			throw ErrorCode.CODE_INVALID.throwServiceException();
		}

		Cart cart = cartRepository.findByCode(cartCode).orElseThrow(ErrorCode.CART_NOT_FOUND::throwServiceException);

		List<CartItem> cartItems = cart.getCartItems();

		if (cartItems.isEmpty()) {
			return new GetItemsByCartResponse(0L, List.of());
		}

		List<String> productCodes = cart.getCartItems().stream()
			.map(CartItem::getProductCode)
			.distinct()
			.toList();

		List<CartProductListResponse> cartProducts = productFeignClient.productListByCodes(productCodes);

		long totalAmount = cartProducts.stream()
			.mapToLong(CartProductListResponse::price)
			.sum();

		return new GetItemsByCartResponse(totalAmount, cartProducts);
	}

	@Override
	@Transactional
	public int deleteItemsByCart(String cartCode, DeleteItemsByCartRequest request) {
		if (!Validators.isValidUuid(cartCode)) {
			throw ErrorCode.CODE_INVALID.throwServiceException();
		}

		if (request.cartProductCodes().isEmpty()) {
			throw ErrorCode.CART_ITEM_DELETE_NOT_SELECTED.throwServiceException();
		}

		cartRepository.findByCode(cartCode).orElseThrow(ErrorCode.CART_NOT_FOUND::throwServiceException);

		int result = cartItemRepository.deleteCartItemByProductCodes(cartCode, request.cartProductCodes());

		if (result != request.cartProductCodes().size()) {
			throw ErrorCode.DELETE_CART_ITEM_FAILED.throwServiceException();
		}

		return result;
	}
}
