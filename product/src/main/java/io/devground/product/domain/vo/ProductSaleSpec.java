package io.devground.product.domain.vo;

public record ProductSaleSpec(

	Long price,
	ProductStatus productStatus
) {

	public ProductSaleSpec {
		if (price == null) {
			DomainErrorCode.PRICE_MUST_BE_INPUT.throwException();
		}

		if (price <= 0) {
			DomainErrorCode.PRICE_MUST_BE_POSITIVE.throwException();
		}
	}
}
