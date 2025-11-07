package io.devground.dbay.domain.cart.cart.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.vo.ErrorCode;
import io.devground.core.util.Validators;
import io.devground.dbay.domain.cart.cart.model.entity.Cart;
import io.devground.dbay.domain.cart.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;

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
}
