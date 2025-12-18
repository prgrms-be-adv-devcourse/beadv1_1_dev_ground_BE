package io.devground.dbay.domain.order.order.model.vo;

import java.util.List;

public record CartProductsRequest(
	List<String> productCodes
) {
}
