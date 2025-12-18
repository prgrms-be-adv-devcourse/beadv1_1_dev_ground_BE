package io.devground.dbay.cart.application.port.out.ai;

import io.devground.dbay.cart.domain.vo.CartRecommendVectorHits;

import java.util.List;

public interface VectorSearchPort {
    List<CartRecommendVectorHits> vectorSearch(String query, int topK, int limit);
}
