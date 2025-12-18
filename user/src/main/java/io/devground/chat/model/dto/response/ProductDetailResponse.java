package io.devground.chat.model.dto.response;

import lombok.Builder;
import lombok.NonNull;

import java.util.List;

@Builder
public record ProductDetailResponse(

        String productCode,
        String productSaleCode,
        String sellerCode,
        String title,
        String description,
        String categoryPath,
        long price,
        String productStatus,
        List<String> imageUrls
) {
}

