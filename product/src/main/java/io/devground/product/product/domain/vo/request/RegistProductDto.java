package io.devground.product.product.domain.vo.request;

import java.util.List;

public record RegistProductDto(

	Long categoryId,
	String title,
	String description,
	Long price,
	List<String> imageExtensions
) {
}