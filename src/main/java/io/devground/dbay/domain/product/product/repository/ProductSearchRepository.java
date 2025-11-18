package io.devground.dbay.domain.product.product.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import io.devground.dbay.domain.product.product.model.entity.ProductDocument;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {
}
