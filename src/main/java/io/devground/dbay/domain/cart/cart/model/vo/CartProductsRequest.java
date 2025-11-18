package io.devground.dbay.domain.cart.cart.model.vo;

import java.util.List;

public record CartProductsRequest(
	List<String> productCodes
) {
}
