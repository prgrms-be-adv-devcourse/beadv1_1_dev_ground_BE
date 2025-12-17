package io.devground.chat.model.dto.response;

public record CartProductsResponse(
        String productCode,
        String productSaleCode,
        String sellerCode,
        String title,
        String description,
        String thumbnail,
        String categoryName,
        long price
) {
}
