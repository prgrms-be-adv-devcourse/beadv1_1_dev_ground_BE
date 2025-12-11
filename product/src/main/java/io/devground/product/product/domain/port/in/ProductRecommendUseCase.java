package io.devground.product.product.domain.port.in;

import io.devground.product.product.domain.vo.response.ProductRecommendResponse;

public interface ProductRecommendUseCase {

	ProductRecommendResponse recommendByUserView(String userCode, Integer size);

	ProductRecommendResponse recommendByProductDetail(String productCode, Integer size);
}
