package io.devground.product.product.domain.vo.response;

public record AdminCategoryResponse(

	long id,
	String name,
	int depth,
	Long parentId,
	boolean isLeaf
) {
}
