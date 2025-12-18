package io.devground.dbay.domain.product.product.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import io.devground.dbay.domain.product.product.model.entity.ProductDocument;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {
}
