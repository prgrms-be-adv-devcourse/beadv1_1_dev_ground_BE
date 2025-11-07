package io.devground.dbay.domain.cart.cart.service;

import io.devground.dbay.domain.cart.cart.model.entity.Cart;

public interface CartService {
	Cart createCart(String userCode);
}
