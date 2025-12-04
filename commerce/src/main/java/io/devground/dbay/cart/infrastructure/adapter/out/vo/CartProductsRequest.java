package io.devground.dbay.cart.infrastructure.adapter.out.vo;

import java.util.List;

public record CartProductsRequest(
	List<String> productCodes
) {
}
