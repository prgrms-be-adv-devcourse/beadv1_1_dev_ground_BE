package io.devground.core.event.cart;

public record CartCreatedFailedEvent(
	String userCode,
	String msg
) {
}
