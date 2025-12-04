package io.devground.dbay.ddddeposit.domain.pagination;

public record SortSpec(

	String property,
	Direction direction
) {
	public enum Direction {
		ASC, DESC
	}
}
