package io.devground.core.events.cart;

public record CartCreatedEvent(
	String userCode,
	String carCode
) {
}
