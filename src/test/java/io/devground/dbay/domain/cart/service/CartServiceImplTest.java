package io.devground.dbay.domain.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.cart.cart.model.entity.Cart;
import io.devground.dbay.domain.cart.cart.model.vo.AddCartItemRequest;
import io.devground.dbay.domain.cart.cart.model.vo.AddCartItemResponse;
import io.devground.dbay.domain.cart.cart.repository.CartRepository;
import io.devground.dbay.domain.cart.cart.service.CartServiceImpl;
import io.devground.dbay.domain.cart.cartItem.model.entity.CartItem;
import io.devground.dbay.domain.cart.cartItem.repository.CartItemRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

	@Mock
	private CartRepository cartRepository;

	@Mock
	private CartItemRepository cartItemRepository;

	@InjectMocks
	private CartServiceImpl cartService;

	@Test
	@DisplayName("성공_장바구니 생성")
	void createCart_success() {

		String userCode = UUID.randomUUID().toString();
		given(cartRepository.existsByUserCode(userCode)).willReturn(false);

		Cart cart = Cart.builder()
			.userCode(userCode)
			.build();
		given(cartRepository.save(any(Cart.class))).willReturn(cart);

		Cart result = cartService.createCart(userCode);

		assertThat(result.getUserCode()).isEqualTo(userCode);
		verify(cartRepository).existsByUserCode(userCode);
		verify(cartRepository).save(any(Cart.class));
	}

	@Test
	@DisplayName("실패_장바구니 코드 유효한 값")
	void createCart_throwException_whenInvalidCode() {
		String userCode = "invalid-code";

		assertThatThrownBy(() -> cartService.createCart(userCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
			});
	}

	@Test
	@DisplayName("실패_장바구니 이미 존재")
	void createCart_throwException_whenCartAlreadyExists() {
		String userCode = UUID.randomUUID().toString();
		given(cartRepository.existsByUserCode(userCode)).willReturn(true);

		assertThatThrownBy(() -> cartService.createCart(userCode))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CART_ALREADY_EXIST);
			});
	}

	@Test
	@DisplayName("성공_장바구니 상품 추가")
	void addItem_success() {
		String userCode = UUID.randomUUID().toString();
		String cartCode = UUID.randomUUID().toString();
		String productCode = UUID.randomUUID().toString();

		given(cartRepository.existsByCode(cartCode)).willReturn(true);

		given(cartItemRepository.existsByCart_CodeAndProductCode(cartCode, productCode))
			.willReturn(false);

		Cart cart = Cart.builder()
			.userCode(userCode)
			.build();

		ReflectionTestUtils.setField(cart, "code", cartCode);

		given(cartRepository.findByCode(cartCode)).willReturn(cart);

		given(cartItemRepository.save(any(CartItem.class)))
			.willAnswer(i -> i.getArgument(0));

		AddCartItemRequest request = new AddCartItemRequest(productCode);

		CartItem result = cartService.addItem(cartCode, request);

		assertThat(result.getCart().getCode()).isEqualTo(cartCode);
		assertThat(result.getProductCode()).isEqualTo(productCode);

		verify(cartRepository).existsByCode(cartCode);
		verify(cartItemRepository).existsByCart_CodeAndProductCode(cartCode, productCode);
		verify(cartRepository).findByCode(cartCode);
		verify(cartItemRepository).save(any(CartItem.class));
	}

	@Test
	@DisplayName("실패_장바구니,상품코드 유효한 값")
	void addItem_throwException_whenInvalidCode() {
		String cartCode = "invalid-uuid";
		String productCode = "invalid-uuid";

		AddCartItemRequest request = new AddCartItemRequest(productCode);

		assertThatThrownBy(() -> cartService.addItem(cartCode, request))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CODE_INVALID);
			});
	}

	@Test
	@DisplayName("실패_장바구니 없음")
	void addItem_throwException_whenCartNotExists() {
		String cartCode = UUID.randomUUID().toString();
		String productCode = UUID.randomUUID().toString();

		AddCartItemRequest request = new AddCartItemRequest(productCode);

		given(cartRepository.existsByCode(cartCode)).willReturn(false);

		assertThatThrownBy(() -> cartService.addItem(cartCode, request))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CART_NOT_FOUND);
			});
	}

	@Test
	@DisplayName("실패_장바구니에 상품 이미 존재")
	void addItem_thrownException_whenCartItemAlreadyExists() {
		String cartCode = UUID.randomUUID().toString();
		String productCode = UUID.randomUUID().toString();

		AddCartItemRequest request = new AddCartItemRequest(productCode);

		given(cartRepository.existsByCode(cartCode)).willReturn(true);
		given(cartItemRepository.existsByCart_CodeAndProductCode(cartCode, productCode)).willReturn(true);

		assertThatThrownBy(() -> cartService.addItem(cartCode, request))
			.isInstanceOf(ServiceException.class)
			.satisfies(ex -> {
				ServiceException se = (ServiceException) ex;
				assertThat(se.getErrorCode()).isEqualTo(ErrorCode.CART_ITEM_ALREADY_EXIST);
			});
	}
}
