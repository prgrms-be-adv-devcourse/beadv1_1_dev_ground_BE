package io.devground.dbay.domain.cart.cart.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.vo.ErrorCode;
import io.devground.core.util.Validators;
import io.devground.dbay.domain.cart.cart.model.entity.Cart;
import io.devground.dbay.domain.cart.cart.model.vo.AddCartItemRequest;
import io.devground.dbay.domain.cart.cart.model.vo.CartProductsRequest;
import io.devground.dbay.domain.cart.cart.model.vo.CartProductsResponse;
import io.devground.dbay.domain.cart.cart.model.vo.DeleteItemsByCartRequest;
import io.devground.dbay.domain.cart.cart.model.vo.GetItemsByCartResponse;
import io.devground.dbay.domain.cart.cart.model.vo.ProductDetailResponse;
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

		cartRepository.findByUserCode(userCode).ifPresent(c -> {
			throw ErrorCode.CART_ALREADY_EXIST.throwServiceException();
		});

		Cart cart = Cart.builder()
			.userCode(userCode)
			.build();

		return cartRepository.save(cart);
	}

	@Override
	@Transactional
	public CartItem addItem(String userCode, AddCartItemRequest request) {

		if (!Validators.isValidUuid(userCode) || !Validators.isValidUuid(request.productCode())) {
			throw ErrorCode.CODE_INVALID.throwServiceException();
		}

		Cart cart = cartRepository.findByUserCode(userCode).orElseThrow(ErrorCode.CART_NOT_FOUND::throwServiceException);

		if (cartItemRepository.existsByCartAndProductCode(cart, request.productCode())) {
			throw ErrorCode.CART_ITEM_ALREADY_EXIST.throwServiceException();
		}

		ProductDetailResponse productDetail = productFeignClient.getProductDetail(request.productCode())
			.throwIfNotSuccess()
			.data();

		if (productDetail.productStatus().equals("SOLD")) {
			throw ErrorCode.SOLD_PRODUCT_CANNOT_PURCHASE.throwServiceException();
		}

		CartItem cartItem = CartItem.builder()
			.cart(cart)
			.productCode(request.productCode())
			.build();

		return cartItemRepository.save(cartItem);
	}

	@Override
	@Transactional(readOnly = true)
	public GetItemsByCartResponse getItemsByCart(String userCode) {
		if (!Validators.isValidUuid(userCode)) {
			throw ErrorCode.CODE_INVALID.throwServiceException();
		}

		Cart cart = cartRepository.findByUserCode(userCode).orElseThrow(ErrorCode.CART_NOT_FOUND::throwServiceException);

		List<CartItem> cartItems = cartItemRepository.findByCart(cart);

		if (cartItems.isEmpty()) {
			return new GetItemsByCartResponse(0L, List.of());
		}

		List<String> productCodes = cartItems.stream()
			.map(CartItem::getProductCode)
			.distinct()
			.toList();

		List<CartProductsResponse> cartProducts = productFeignClient.getCartProducts(new CartProductsRequest(productCodes))
			.throwIfNotSuccess().data();

		long totalAmount = cartProducts.stream()
			.mapToLong(CartProductsResponse::price)
			.sum();

		return new GetItemsByCartResponse(totalAmount, cartProducts);
	}

	@Override
	@Transactional
	public int deleteItemsByCart(String userCode, DeleteItemsByCartRequest request) {
		if (!Validators.isValidUuid(userCode)) {
			throw ErrorCode.CODE_INVALID.throwServiceException();
		}

		if (request.cartProductCodes().isEmpty()) {
			throw ErrorCode.CART_ITEM_DELETE_NOT_SELECTED.throwServiceException();
		}

		Cart cart = cartRepository.findByUserCode(userCode).orElseThrow(ErrorCode.CART_NOT_FOUND::throwServiceException);

		int result = cartItemRepository.deleteCartItemByProductCodes(cart, request.cartProductCodes());

		if (result != request.cartProductCodes().size()) {
			throw ErrorCode.DELETE_CART_ITEM_FAILED.throwServiceException();
		}

		return result;
	}

	@Override
	@Transactional
	public Cart deleteCart(String userCode) {
		if (!Validators.isValidUuid(userCode)) {
			throw ErrorCode.CODE_INVALID.throwServiceException();
		}

		Cart cart = cartRepository.findByUserCode(userCode)
			.orElseThrow(ErrorCode.CART_NOT_FOUND::throwServiceException);

		cartItemRepository.deleteCartItemByCartCode(cart);

		cart.delete();

		return cart;
	}
}
