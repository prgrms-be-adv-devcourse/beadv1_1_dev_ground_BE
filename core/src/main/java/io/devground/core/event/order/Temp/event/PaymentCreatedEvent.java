package io.devground.core.event.order.Temp.event;

public record PaymentCreatedEvent(
	String userCode,
	Long amount,
	String type,
	String orderCode
) {
}
