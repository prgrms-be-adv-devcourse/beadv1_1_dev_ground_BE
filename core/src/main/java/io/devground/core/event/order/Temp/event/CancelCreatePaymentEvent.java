package io.devground.core.event.order.Temp.event;

public record CancelCreatePaymentEvent(
	String userCode,
	String orderCode,
	String msg
) {
}
