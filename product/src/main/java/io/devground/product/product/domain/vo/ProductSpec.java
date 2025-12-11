package io.devground.product.product.domain.vo;

public record ProductSpec(

	String title,
	String description
) {

	public ProductSpec {
		if (title == null || title.isBlank()) {
			ProductDomainErrorCode.TITLE_MUST_BE_INPUT.throwException();
		}

		if (description == null || description.isBlank()) {
			ProductDomainErrorCode.DESCRIPTION_MUST_BE_INPUT.throwException();
		}
	}
}
