package io.devground.dbay.order.domain.vo;

public record Progress(
        long paidOrder,
        long paidToDelivery,
        long deliveryOrder,
        long deliveryToDelivered
) {
}
