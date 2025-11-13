package io.devground.dbay.domain.order.order.model.vo;

public enum OrderStatus {
	PENDING,
	PAID,
	START_DELIVERY,
	DELIVERED,
	CONFIRMED,
	CANCELLED;

	public boolean isCancellable() {
		return switch (this) {
			case PENDING,PAID, START_DELIVERY -> true;
			default -> false;
		};
	}
}
