package io.devground.dbay.domain.product.product.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SuggestOption(

	@Schema(description = "제안 텍스트", example = "삼성 갤럭시 S25 Ultra")
	String text,

	@Schema(description = "관련도 점수(COMPLETION, PHRASE에서 사용)", example = "5.5")
	Float score,

	@Schema(description = "해당 검색어의 상품 개수(RELATED에서 사용)", example = "10")
	Long productCount
) {
}
