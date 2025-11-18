package io.devground.dbay.domain.product.product.model.dto;

import lombok.Builder;

@Builder
public record SuggestOption(

	String text,
	Float score,
	Long productCount
) {
}
