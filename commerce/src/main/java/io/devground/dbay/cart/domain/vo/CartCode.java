package io.devground.dbay.cart.domain.vo;

import io.devground.dbay.cart.domain.exception.DomainError;

import java.util.UUID;

public record CartCode(String value) {
    public CartCode {
        if (value == null || value.trim().isEmpty()) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }
    }
    public static CartCode create() {
        return new CartCode(UUID.randomUUID().toString());
    }
}
