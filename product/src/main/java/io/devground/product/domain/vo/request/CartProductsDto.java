package io.devground.product.domain.vo.request;

import java.util.List;

public record CartProductsDto(

	List<String> productCodes
) {
}