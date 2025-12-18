package io.devground.core.event.cart;

public record CartDeletedFailedEvent(
	String userCode,
	String msg
) {
}
