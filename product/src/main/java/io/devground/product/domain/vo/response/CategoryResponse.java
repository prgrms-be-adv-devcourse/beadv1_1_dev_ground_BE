package io.devground.product.domain.vo.response;

public record CategoryResponse(
	long id,
	String name,
	boolean isLeaf
) {
}
