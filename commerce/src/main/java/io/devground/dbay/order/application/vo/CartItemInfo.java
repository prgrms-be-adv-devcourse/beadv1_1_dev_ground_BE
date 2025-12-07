package io.devground.dbay.order.application.vo;

import io.devground.dbay.order.application.exception.ServiceError;

public record CartItemInfo(
        String productCode,
        String productName,
        Long productPrice
) {
    public CartItemInfo {
        if (productCode == null) {
            throw ServiceError.CODE_INVALID.throwServiceException();
        }

        if (productName == null) {
            throw ServiceError.PRODUCT_NOT_FOUND.throwServiceException();
        }

        if (productPrice == null || productPrice <= 0) {
            throw ServiceError.AMOUNT_MUST_BE_POSITIVE.throwServiceException();
        }
    }
}
