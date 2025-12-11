package io.devground.dbay.order.application.vo;

import io.devground.dbay.order.domain.vo.ProductStatus;

public record ProductSnapShot(
        String productCode,
        String sellerCode,
        String productName,
        long productPrice,
        ProductStatus productStatus
) {
}
