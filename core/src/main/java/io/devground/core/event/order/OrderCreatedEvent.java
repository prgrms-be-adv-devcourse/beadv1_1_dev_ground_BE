package io.devground.core.event.order;

import java.util.List;

public record OrderCreatedEvent(
	String userCode,
	String orderCode,
	Long totalAmount,
	List<String> productCodes
) {
}
