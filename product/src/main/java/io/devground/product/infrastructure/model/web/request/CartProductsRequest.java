package io.devground.product.infrastructure.model.web.request;

import java.util.List;

public record CartProductsRequest(
	List<String> productCodes
) {
}