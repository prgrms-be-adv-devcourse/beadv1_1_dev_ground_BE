package io.devground.chat.model.dto.response;

//사용예정
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
