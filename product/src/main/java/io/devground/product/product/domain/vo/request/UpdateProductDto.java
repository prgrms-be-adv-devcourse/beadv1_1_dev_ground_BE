package io.devground.product.product.domain.vo.request;

import java.util.List;

public record UpdateProductDto(

	String title,
	String description,
	Long price,
	List<String> deleteUrls,
	List<String> newImageExtensions
) {
}