package io.devground.product.product.application.port.out;

import java.util.List;

import io.devground.product.product.domain.model.Product;
import io.devground.product.product.domain.vo.ProductRecommendSpec;

public interface ProductVectorPort {

	void prepareVector(Product product);

	void updateVector(Product product);

	void deleteVector(Product product);

	List<ProductRecommendSpec> recommendByUserView(List<Product> products, int size);

	List<ProductRecommendSpec> recommendByProductDetail(Product product, int size);
}
