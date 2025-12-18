package io.devground.product.product.domain.vo.response;

public record CategoryResponse(
	long id,
	String name,
	boolean isLeaf
) {
}
