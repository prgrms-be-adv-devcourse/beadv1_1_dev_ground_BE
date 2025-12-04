package io.devground.product.infrastructure.adapter.out;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import io.devground.product.infrastructure.model.persistence.ProductDocument;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {
}
