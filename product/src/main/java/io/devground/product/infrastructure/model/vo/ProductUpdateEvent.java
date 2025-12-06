package io.devground.product.infrastructure.model.vo;

import io.devground.product.domain.model.Product;

public record ProductUpdateEvent(

	Product product
) {
}
