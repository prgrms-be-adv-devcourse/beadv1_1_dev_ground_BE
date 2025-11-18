package io.devground.dbay.domain.product.product.model.dto;

import java.util.List;

import io.devground.dbay.domain.product.product.model.vo.SuggestType;
import lombok.Builder;

@Builder
public record ProductSuggestResponse(

	String originalKeyword,

	SuggestType type,

	List<SuggestOption> suggestions
) {
}
