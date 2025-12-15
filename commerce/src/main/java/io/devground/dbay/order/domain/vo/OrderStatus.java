package io.devground.dbay.order.domain.vo;

public enum OrderStatus {
    ALL,
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
