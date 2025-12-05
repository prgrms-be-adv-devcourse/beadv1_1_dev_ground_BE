package io.devground.product.domain.vo;

public record ProductSpec(

	String title,
	String description
) {

	public ProductSpec {
		if (title == null || title.isBlank()) {
			DomainErrorCode.TITLE_MUST_BE_INPUT.throwException();
		}

		if (description == null || description.isBlank()) {
			DomainErrorCode.DESCRIPTION_MUST_BE_INPUT.throwException();
		}
	}
}
