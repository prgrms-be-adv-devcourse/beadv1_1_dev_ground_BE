package io.devground.product.application.model;

import java.util.List;

public record RegistProductDto(

	Long categoryId,
	String title,
	String description,
	Long price,
	List<String> imageExtensions
) {
}