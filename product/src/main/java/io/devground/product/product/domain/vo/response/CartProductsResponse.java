package io.devground.product.product.domain.vo.response;

public record CartProductsResponse(

	String productCode,
	String productSaleCode,
	String sellerCode,
	String title,
	long price
) {
}
