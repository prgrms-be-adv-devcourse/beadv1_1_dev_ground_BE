package io.devground.dbay.cart.cart.service;

import io.devground.dbay.cart.cart.model.entity.Cart;
import io.devground.dbay.cart.cart.model.vo.AddCartItemRequest;
import io.devground.dbay.cart.cart.model.vo.DeleteItemsByCartRequest;
import io.devground.dbay.cart.cart.model.vo.GetItemsByCartResponse;
import io.devground.dbay.cart.cartItem.model.entity.CartItem;

public interface CartService {
	Cart createCart(String userCode);

	CartItem addItem(String userCode, AddCartItemRequest request);

	GetItemsByCartResponse getItemsByCart(String cartCode);

	int deleteItemsByCart(String cartCode, DeleteItemsByCartRequest request);

	Cart deleteCart(String userCode);
}
