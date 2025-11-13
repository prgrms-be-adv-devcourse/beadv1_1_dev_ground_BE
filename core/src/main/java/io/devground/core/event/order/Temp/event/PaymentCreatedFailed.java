package io.devground.core.event.order.Temp.event;

public record PaymentCreatedFailed(
	String orderCode,
	String userCode,
	String msg
) {
}
