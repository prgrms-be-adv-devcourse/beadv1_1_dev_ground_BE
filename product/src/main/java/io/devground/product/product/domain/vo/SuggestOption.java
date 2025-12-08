package io.devground.product.product.domain.vo;

public record SuggestOption(

	String text,
	Float score,
	Long productCount
) {
}