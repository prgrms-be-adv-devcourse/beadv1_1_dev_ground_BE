package io.devground.product.product.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.devground.product.product.application.port.out.ProductVectorPort;
import io.devground.product.product.application.port.out.ProductViewPort;
import io.devground.product.product.application.port.out.persistence.ProductPersistencePort;
import io.devground.product.product.domain.model.Product;
import io.devground.product.product.domain.port.in.ProductRecommendUseCase;
import io.devground.product.product.domain.vo.ProductRecommendSpec;
import io.devground.product.product.domain.vo.RecommendType;
import io.devground.product.product.domain.vo.response.ProductRecommendResponse;
import io.devground.product.product.infrastructure.util.ProductVectorUtil;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductRecommendApplication implements ProductRecommendUseCase {

	private static final int DEFAULT_SIZE = 10;
	private static final int MAX_SIZE = 30;
	private static final int MIN_VALID_USER_VIEW_COUNT = 5;
	private static final int RESULT_CODES_SIZE = 10;

	private final ProductPersistencePort productPort;
	private final ProductViewPort viewPort;
	private final ProductVectorPort vectorPort;

	@Override
	public ProductRecommendResponse recommendByUserView(String userCode, Integer size) {

		int convertedSize = this.convertToSafeSize(size);

		List<ProductRecommendSpec> recommendSpecs = this.tryRecommendByUserView(userCode, convertedSize);

		if (!CollectionUtils.isEmpty(recommendSpecs)) {
			return new ProductRecommendResponse(RecommendType.USER_VIEW_HISTORY, recommendSpecs);
		}

		List<ProductRecommendSpec> fallbackSpecs = this.recommendFallbackPopular(convertedSize);

		return new ProductRecommendResponse(RecommendType.FALLBACK_POPULAR, fallbackSpecs);
	}

	@Override
	public ProductRecommendResponse recommendByProductDetail(String productCode, Integer size) {

		int convertedSize = this.convertToSafeSize(size);

		Product product = productPort.getProductByCode(productCode);

		List<ProductRecommendSpec> recommendSpec = vectorPort.recommendByProductDetail(product, convertedSize);

		if (CollectionUtils.isEmpty(recommendSpec)) {
			List<ProductRecommendSpec> fallbackSpecs = this.recommendFallbackPopular(convertedSize);

			return new ProductRecommendResponse(RecommendType.FALLBACK_POPULAR, fallbackSpecs);
		}

		return new ProductRecommendResponse(RecommendType.PRODUCT_DETAIL, recommendSpec);
	}

	private List<ProductRecommendSpec> tryRecommendByUserView(String userCode, int size) {

		if (!StringUtils.hasText(userCode)) {
			return List.of();
		}

		List<String> productCodes = viewPort.getLatestViewedProductCodes(userCode, RESULT_CODES_SIZE);

		if (CollectionUtils.isEmpty(productCodes) || productCodes.size() < MIN_VALID_USER_VIEW_COUNT) {
			return List.of();
		}

		List<Product> products = productPort.getProductsByCodes(productCodes);

		if (CollectionUtils.isEmpty(products)) {
			return List.of();
		}

		return vectorPort.recommendByUserView(products, size);
	}

	private List<ProductRecommendSpec> recommendFallbackPopular(int size) {

		List<String> productCodes = viewPort.getTopProductCodes(size);

		if (CollectionUtils.isEmpty(productCodes)) {
			return List.of();
		}

		List<Product> products = productPort.getProductsByCodes(productCodes);

		if (CollectionUtils.isEmpty(products)) {
			return List.of();
		}

		Map<String, Product> productMap = products.stream()
			.collect(Collectors.toMap(Product::getCode, product -> product));

		List<ProductRecommendSpec> sortedSpecs = new ArrayList<>();
		for (String productCode : productCodes) {
			Product product = productMap.get(productCode);
			if (product != null) {
				sortedSpecs.add(ProductVectorUtil.toRecommendSpec(product));
			}
		}

		return sortedSpecs.stream().limit(size).toList();
	}

	private int convertToSafeSize(Integer size) {

		if (size == null || size <= 0 || size > MAX_SIZE) {
			return DEFAULT_SIZE;
		}

		return size;
	}
}
