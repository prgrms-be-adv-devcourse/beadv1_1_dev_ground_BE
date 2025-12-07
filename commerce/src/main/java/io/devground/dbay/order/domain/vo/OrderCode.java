package io.devground.dbay.order.domain.vo;

import io.devground.dbay.cart.domain.exception.DomainError;

import java.util.UUID;

public record OrderCode(String value) {
    public OrderCode {
        if (value == null || value.trim().isEmpty()) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }
    }
    public static OrderCode create() {
        return new OrderCode(UUID.randomUUID().toString());
    }
}
