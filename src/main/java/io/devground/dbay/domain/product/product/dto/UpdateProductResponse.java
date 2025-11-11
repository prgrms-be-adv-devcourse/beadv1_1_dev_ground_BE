package io.devground.dbay.domain.product.product.dto;

import java.net.URL;
import java.util.List;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record UpdateProductResponse(

	@NonNull
	String productCode,

	@NonNull
	String productSaleCode,

	@NonNull
	String sellerCode,

	@NonNull
	String title,

	@NonNull
	String description,

	long price,

	List<URL> presignedUrl
) {
}
