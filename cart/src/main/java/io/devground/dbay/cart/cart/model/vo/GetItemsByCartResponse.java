package io.devground.dbay.cart.cart.model.vo;



import java.util.List;

public record GetItemsByCartResponse(
	Long totalAmount,
	List<CartProductsResponse> productLists
) {
}
