package io.devground.product.application.port.out.persistence;

import io.devground.product.domain.model.Product;

public interface ProductSearchPort {

	void indexProduct(Product product);
}
