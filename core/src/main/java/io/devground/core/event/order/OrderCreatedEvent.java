package io.devground.core.event.order;

public record OrderCreatedEvent(
	String userCode,
	String orderCode,
	Long totalAmount
) {
}
