package io.devground.dbay.cart.domain.vo;

import io.devground.dbay.cart.domain.exception.DomainError;

public record ProductCode(String value) {
    public ProductCode {
        if (value == null || value.isBlank()) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }
    }
}
