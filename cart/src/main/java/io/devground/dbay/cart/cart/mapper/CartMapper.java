package io.devground.dbay.cart.cart.mapper;

import io.devground.dbay.cart.cart.model.vo.AddCartItemResponse;
import io.devground.dbay.cart.cartItem.model.entity.CartItem;
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
