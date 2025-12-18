package io.devground.dbay.cart.application.port.out.ai;

import io.devground.dbay.cart.domain.vo.CartContext;

public interface PromptPort {
    String generateRecommendPrompt(CartContext ctx);
}
