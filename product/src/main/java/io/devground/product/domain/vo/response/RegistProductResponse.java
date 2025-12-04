package io.devground.product.domain.vo.response;

import java.net.URL;
import java.util.List;

public record RegistProductResponse(

	String productCode,
	String productSaleCode,
	String sellerCode,
	String title,
	String description,
	long price,
	List<URL> presignedUrls
) {
}