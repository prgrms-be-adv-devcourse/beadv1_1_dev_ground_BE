package io.devground.dbay.order.application.vo;

import io.devground.dbay.order.domain.vo.ProductCode;

public record ProductInfoSnapShot(
        ProductCode productCode,
        String productSaleCode,
        String sellerCode,
        String title,
        String thumbnail,
        long price,
        String description,
        String categoryName
) {
}
