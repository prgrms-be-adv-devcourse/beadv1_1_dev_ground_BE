package io.devground.payments.deposit.domain.pagination;

public record SortSpec(

	String property,
	Direction direction
) {
	public enum Direction {
		ASC, DESC
	}
}
