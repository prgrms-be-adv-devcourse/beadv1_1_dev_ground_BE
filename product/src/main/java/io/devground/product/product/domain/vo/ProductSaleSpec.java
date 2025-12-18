package io.devground.product.product.domain.vo;

public record ProductSaleSpec(

	Long price,
	ProductStatus productStatus
) {

	public ProductSaleSpec {
		if (price == null) {
			ProductDomainErrorCode.PRICE_MUST_BE_INPUT.throwException();
		}

		if (price <= 0) {
			ProductDomainErrorCode.PRICE_MUST_BE_POSITIVE.throwException();
		}
	}
}
