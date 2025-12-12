package io.devground.dbay.cart.domain.vo;

import io.devground.dbay.cart.application.vo.ProductInfoSnapShot;

import java.util.List;
import java.util.stream.Collectors;

public record CartContext(
        List<ProductInfoSnapShot> items,
        String prompt
) {
    public static CartContext of(List<ProductInfoSnapShot> items) {
        String prompt = items.stream()
                .map(i -> "- 상품명: %s, 카테고리: %s, 설명: %s".formatted(
                        i.title(),i.categoryName(),i.description()
                )).collect(Collectors.joining("\n"));
        return new CartContext(items, prompt);
    }
}
