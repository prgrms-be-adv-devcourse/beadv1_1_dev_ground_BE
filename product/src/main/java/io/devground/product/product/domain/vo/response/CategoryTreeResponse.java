package io.devground.product.product.domain.vo.response;

import java.util.List;

public record CategoryTreeResponse(

	long id,
	String name,
	int depth,
	boolean isLeaf,
	List<CategoryTreeResponse> children
) {
}
