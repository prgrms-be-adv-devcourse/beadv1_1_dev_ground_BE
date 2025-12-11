package io.devground.product.product.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.product.product.domain.port.in.ProductRecommendUseCase;
import io.devground.product.product.domain.vo.response.ProductRecommendResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductRecommendApplication implements ProductRecommendUseCase {

	@Override
	public ProductRecommendResponse recommendByUserView(String userCode, Integer size) {

		throw new UnsupportedOperationException("구현 중");
	}

	@Override
	public ProductRecommendResponse recommendByProductDetail(String productCode, Integer size) {

		throw new UnsupportedOperationException("구현 중");
	}
}
