package io.devground.dbay.cart.infrastructure.adapter.out.ai;

import io.devground.dbay.cart.application.port.out.ai.PromptPort;
import io.devground.dbay.cart.domain.vo.CartContext;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CartPromptAdapter implements PromptPort {

    private final ChatClient chatClient;

    @Override
    public String generateRecommendPrompt(CartContext ctx) {
        PromptTemplate promptTemplate = new PromptTemplate("""
        당신은 장바구니 기반 추천 상품을 만드는 검색어 생성 전문가입니다.
        
        장바구니 목록:
        {cartSummary}
        
        위 상품들과 함께 살 만한 상품 키워드 10개를 콤마로 구분해 짧게 만들어줘
        """);

        Map<String, Object> params = Map.of("cartSummary", ctx.prompt());

        Prompt prompt = promptTemplate.create(params);

        return Objects.requireNonNull(chatClient.prompt(prompt).call().chatResponse()).getResult().getOutput().getText();
    }
}
