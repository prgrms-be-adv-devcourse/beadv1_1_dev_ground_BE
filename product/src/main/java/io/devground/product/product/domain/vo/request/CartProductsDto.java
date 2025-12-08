package io.devground.product.product.domain.vo.request;

import java.util.List;

public record CartProductsDto(

	List<String> productCodes
) {
}