package io.devground.product.product.domain.vo.pagination;

public record SortSpec(

	String property,
	Direction direction
) {
	public enum Direction {
		ASC, DESC
	}
}
