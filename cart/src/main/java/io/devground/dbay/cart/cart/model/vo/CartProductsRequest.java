package io.devground.dbay.cart.cart.model.vo;

import java.util.List;

public record CartProductsRequest(
	List<String> productCodes
) {
}
