package io.devground.product.product.domain.vo.request;

public record ProductSuggestDto(

	String keyword,
	Long categoryId,
	Boolean includeSold,
	Integer size
) {
	public ProductSuggestDto {

		if (includeSold == null) {
			includeSold = false;
		}

		if (size == null || size <= 0 || size > 100) {
			size = 10;
		}
	}
}
