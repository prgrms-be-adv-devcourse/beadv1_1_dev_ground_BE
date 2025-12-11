package io.devground.product.product.application.port.out;

import io.devground.product.product.domain.model.Product;

public interface ProductVectorPort {

	void prepareVector(Product product);

	void updateVector(Product product);

	void deleteVector(Product product);
}
