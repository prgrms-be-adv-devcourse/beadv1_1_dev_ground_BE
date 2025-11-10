package io.devground.dbay.domain.cart.cart.service;

import io.devground.dbay.domain.cart.cart.model.entity.Cart;
import io.devground.dbay.domain.cart.cart.model.vo.AddCartItemRequest;
import io.devground.dbay.domain.cart.cartItem.model.entity.CartItem;

public interface CartService {
	Cart createCart(String userCode);

	CartItem addItem(String cartCode, AddCartItemRequest request);

}
