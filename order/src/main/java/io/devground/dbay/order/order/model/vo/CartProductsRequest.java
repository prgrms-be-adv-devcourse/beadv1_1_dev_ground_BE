package io.devground.dbay.order.order.model.vo;

import java.util.List;

public record CartProductsRequest(
	List<String> productCodes
) {
}
