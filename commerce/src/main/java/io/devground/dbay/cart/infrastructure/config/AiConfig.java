package io.devground.dbay.cart.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.build();
    }


//    @Bean
//    public EmbeddingModel embeddingModel(OpenAiApi openAiApi) {
//        OpenAiEmbeddingOptions opts = OpenAiEmbeddingOptions.builder()
//                .model("text-embedding-3-small")
//                .dimensions(8)
//                .build();
//
//        return new OpenAiEmbeddingModel(openAiApi, opts);
//    }


}
