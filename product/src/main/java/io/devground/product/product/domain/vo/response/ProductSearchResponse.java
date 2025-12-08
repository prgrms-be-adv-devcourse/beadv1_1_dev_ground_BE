package io.devground.product.product.domain.vo.response;

import java.time.LocalDate;

import io.devground.product.product.domain.vo.ProductDomainErrorCode;

public record ProductSearchResponse(

	long id,
	String productCode,

	String title,
	String description,
	String thumbnailUrl,

	String categoryName,
	String categoryFullPath,

	Long price,
	String productStatus,

	LocalDate createdAt,

	Float score
) {
	public ProductSearchResponse {
		if (productCode == null || productCode.isBlank()) {
			ProductDomainErrorCode.PRODUCT_CODE_MUST_BE_INPUT.throwException();
		}

		if (title == null || title.isBlank()) {
			ProductDomainErrorCode.TITLE_MUST_BE_INPUT.throwException();
		}

		if (description == null || description.isBlank()) {
			ProductDomainErrorCode.DESCRIPTION_MUST_BE_INPUT.throwException();
		}

		if (categoryName == null || categoryName.isBlank()) {
			ProductDomainErrorCode.CATEGORY_MUST_BE_INPUT.throwException();
		}

		if (price == null) {
			ProductDomainErrorCode.PRICE_MUST_BE_INPUT.throwException();
		}

		if (price <= 0) {
			ProductDomainErrorCode.PRICE_MUST_BE_POSITIVE.throwException();
		}

		if (productStatus == null || productStatus.isBlank()) {
			ProductDomainErrorCode.PRODUCT_STATUS_MUST_BE_INPUT.throwException();
		}
	}
}