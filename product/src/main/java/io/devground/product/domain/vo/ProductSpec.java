package io.devground.product.domain.vo;

public record ProductSpec(

	String title,
	String description,
	// TODO: thumbnailURL이 여기에 필요한지 숙고
	String thumbnailUrl
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
