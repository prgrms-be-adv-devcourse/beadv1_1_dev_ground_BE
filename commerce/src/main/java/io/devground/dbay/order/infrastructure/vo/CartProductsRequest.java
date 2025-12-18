package io.devground.dbay.order.infrastructure.vo;

import java.util.List;

public record CartProductsRequest(
	List<String> productCodes
) {
}
