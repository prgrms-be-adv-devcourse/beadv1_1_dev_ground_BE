package io.devground.dbay.domain.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.cart.cart.model.entity.Cart;
import io.devground.dbay.domain.cart.cart.repository.CartRepository;
import io.devground.dbay.domain.cart.cart.service.CartServiceImpl;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

	@Mock
	private CartRepository cartRepository;

	@InjectMocks
	private CartServiceImpl cartService;

	@Test
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
}
