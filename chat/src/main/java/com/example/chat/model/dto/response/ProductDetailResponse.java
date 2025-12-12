package com.example.chat.model.dto.response;

import lombok.Builder;
import lombok.NonNull;

import java.util.List;

@Builder
public record ProductDetailResponse(

        @NonNull
        String productCode,

        @NonNull
        String productSaleCode,

        @NonNull
        String sellerCode,

        @NonNull
        String title,

        @NonNull
        String description,

        @NonNull
        String categoryPath,

        long price,

        @NonNull
        String productStatus,

        List<String> imageUrls
) {
}

