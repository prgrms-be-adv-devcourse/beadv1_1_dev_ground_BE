package io.devground.dbay.order.domain.vo.pagination;

public record SortSpec(

	String property,
	Direction direction
) {
	public enum Direction {
		ASC, DESC
	}
}
