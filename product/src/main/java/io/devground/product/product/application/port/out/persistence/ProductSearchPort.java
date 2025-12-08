package io.devground.product.product.application.port.out.persistence;

import io.devground.product.product.domain.model.Product;

public interface ProductSearchPort {

	void prepareSearch(Product product);

	void updateSearch(Product product);

	void deleteSearch(Product product);
}
