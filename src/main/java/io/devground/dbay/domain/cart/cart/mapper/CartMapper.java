package io.devground.dbay.domain.cart.cart.mapper;

import io.devground.dbay.domain.cart.cart.model.entity.Cart;
import io.devground.dbay.domain.cart.cart.model.vo.AddCartItemResponse;
import io.devground.dbay.domain.cart.cartItem.model.entity.CartItem;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CartMapper {

	public AddCartItemResponse toAddCartItemResponse(CartItem cartItem) {
		return new AddCartItemResponse(
			cartItem.getCart().getCode(),
			cartItem.getProductCode()
		);
	}
}
