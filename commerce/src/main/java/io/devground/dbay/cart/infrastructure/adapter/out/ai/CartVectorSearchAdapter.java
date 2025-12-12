package io.devground.dbay.cart.infrastructure.adapter.out.ai;

import io.devground.dbay.cart.application.port.out.ai.VectorSearchPort;
import io.devground.dbay.cart.domain.vo.CartRecommendVectorHits;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class CartVectorSearchAdapter implements VectorSearchPort {

    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;

    @Value("${spring.ai.vectorstore.elasticsearch.index-name}")
    private String indexName;

    @SneakyThrows
    @Override
    public List<CartRecommendVectorHits> vectorSearch(String query, int topK, int limit) {
        SearchRequest searchRequest = SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .build();

        return vectorStore.similaritySearch(searchRequest)
                .stream()
                .map(doc -> new CartRecommendVectorHits(
                        (String) doc.getMetadata().getOrDefault("productCode", ""),
                        doc.getScore() != null ? doc.getScore().floatValue() : 0f
                ))
                .sorted((a, b) -> Float.compare(b.score(), a.score()))
                .limit(limit)
                .toList();
    }
}
