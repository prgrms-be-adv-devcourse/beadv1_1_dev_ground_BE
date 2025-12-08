package io.devground.product.product.domain.vo.response;

import java.util.List;

import io.devground.product.product.domain.vo.SuggestOption;
import io.devground.product.product.domain.vo.SuggestType;

public record ProductSuggestResponse(

	String originalKeyword,
	SuggestType type,
	List<SuggestOption> suggestions
) {
}