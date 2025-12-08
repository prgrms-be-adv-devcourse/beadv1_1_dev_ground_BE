package io.devground.product.product.infrastructure.model.vo;

import io.devground.product.product.domain.model.Product;

public record ProductDeleteEvent(

	Product product
) {
}
