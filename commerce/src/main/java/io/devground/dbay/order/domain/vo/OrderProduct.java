package io.devground.dbay.order.domain.vo;

import io.devground.dbay.order.domain.exception.DomainError;

public record OrderProduct(
        String productCode,
        String sellerCode,
        String productName,
        Long productPrice
) {
    public OrderProduct {
        if (productCode == null || sellerCode == null) {
            throw DomainError.CODE_INVALID.throwDomainException();
        }

        if (productName == null || productName.isEmpty()) {
            throw DomainError.PRODUCT_NOT_FOUND.throwDomainException();
        }

        if (productPrice == null || productPrice <= 0) {
            throw DomainError.AMOUNT_MUST_BE_POSITIVE.throwDomainException();
        }
    }
}
