package io.devground.chat.model.dto.request;

import java.util.List;

public record CartProductsRequest(
        List<String> productCodes
) {
}
