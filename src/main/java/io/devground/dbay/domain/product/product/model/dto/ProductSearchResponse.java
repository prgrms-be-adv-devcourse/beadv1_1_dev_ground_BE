package io.devground.dbay.domain.product.product.model.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record ProductSearchResponse(

	long id,

	@NonNull
	String productCode,

	@NonNull
	String title,

	@NonNull
	String description,

	String thumbnailUrl,

	@NonNull
	String categoryName,

	@NonNull
	String categoryFullPath,

	long price,

	@NonNull
	String productStatus,

	@NonNull
	LocalDateTime createdAt,

	Float score
) {
}
