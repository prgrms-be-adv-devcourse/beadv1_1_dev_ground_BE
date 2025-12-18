package io.devground.dbay.cart.domain.vo;

import io.devground.dbay.cart.domain.exception.DomainError;

public record UserCode(String value) {
    public UserCode {
        if (value == null || value.trim().isEmpty()) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }
    }
}
