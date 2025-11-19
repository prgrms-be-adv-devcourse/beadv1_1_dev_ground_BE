package io.devground.dbay.domain.product.product.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.MDC;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.json.JsonData;
import io.devground.core.model.web.PageDto;
import io.devground.dbay.domain.product.product.mapper.ProductSearchMapper;
import io.devground.dbay.domain.product.product.model.dto.ProductSearchRequest;
import io.devground.dbay.domain.product.product.model.dto.ProductSearchResponse;
import io.devground.dbay.domain.product.product.model.dto.ProductSuggestRequest;
import io.devground.dbay.domain.product.product.model.dto.ProductSuggestResponse;
import io.devground.dbay.domain.product.product.model.dto.SuggestOption;
import io.devground.dbay.domain.product.product.model.entity.ProductDocument;
import io.devground.dbay.domain.product.product.model.vo.SuggestType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "esSearch")
@Transactional(readOnly = true)
public class ProductSearchService {

	private final ElasticsearchOperations operations;

	public PageDto<ProductSearchResponse> searchProducts(ProductSearchRequest request) {

		Query query = buildSearchQuery(request);

		PageRequest pageable = PageRequest.of(request.page() - 1, request.size());

		NativeQueryBuilder queryBuilder = NativeQuery.builder()
			.withQuery(query)
			.withPageable(pageable);

		addSort(queryBuilder, request.sortBy(), request.sortDirection());

		NativeQuery searchQuery = queryBuilder.build();

		// 검색 실행
		SearchHits<ProductDocument> searchHits = operations.search(searchQuery, ProductDocument.class);

		List<ProductSearchResponse> responses = searchHits.getSearchHits().stream()
			.map(ProductSearchMapper::toProductSearchResponse)
			.toList();

		long totalHits = searchHits.getTotalHits();
		int totalPages = (int) Math.ceil((double) totalHits / request.size());

		return new PageDto<>(request.page(), request.size(), totalPages, totalHits, responses);
	}

	public ProductSuggestResponse suggest(ProductSuggestRequest request) {

		if (!StringUtils.hasText(request.keyword())) {
			return ProductSuggestResponse.builder()
				.originalKeyword("")
				.type(request.type())
				.suggestions(Collections.emptyList())
				.build();
		}

		String keyword = request.keyword().trim();
		MDC.put("keyword", keyword);
		MDC.put("suggestType", request.type().name());

		try {
			log.info("키워드 제안 - Keyword={}, Type={}", keyword, request.type());

			return switch (request.type()) {
				case COMPLETION -> completionSuggest(request);
				case RELATED -> relatedTermSuggest(request);
			};
		} catch (Exception e) {
			log.error("키워드 추천 실패 - Keyword={}, Type={}, Exception={}", keyword, request.type(), e.getMessage());

			return ProductSuggestResponse.builder()
				.originalKeyword(keyword)
				.type(request.type())
				.suggestions(Collections.emptyList())
				.build();
		} finally {
			MDC.clear();
		}
	}

	private Query buildSearchQuery(ProductSearchRequest request) {

		BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

		// 1. 삭제되지 않은 상품 필터
		applyExactlyBuilder(boolBuilder, "deleteStatus", FieldValue.of(false));

		// 2. 키워드 검색 (title, description) (가중치 계산)
		// 2-1. 정확한 매칭 위주 (가중치 높음)
		if (StringUtils.hasText(request.keyword())) {
			String keyword = request.keyword().trim();

			BoolQuery.Builder keywordBuilder = new BoolQuery.Builder();

			keywordBuilder.should(s -> s
				.multiMatch(mm -> mm
					.query(keyword)
					.fields(
						"title^4",
						"description^2",
						"categoryName^2",
						"categoryFullPath"
					)
					.type(TextQueryType.BestFields)
				)
			);

			// 2-2. 오타/부분 검색용 (ngram + fuzziness 사용)
			keywordBuilder.should(s -> s
				.multiMatch(mm -> mm
					.query(keyword)
					.fields(
						"title.ngram^3",
						"title.shingle^2",
						"description.ngram^2",
						"categoryFullPath.ngram"
					)
					.type(TextQueryType.BestFields)
					.fuzziness("AUTO")
					.prefixLength(1)
				)
			);

			keywordBuilder.minimumShouldMatch("1");

			boolBuilder.must(m -> m.bool(keywordBuilder.build()));
		}

		// 3. 카테고리 필터
		if (!CollectionUtils.isEmpty(request.categoryIds())) {
			List<FieldValue> categoryValues = request.categoryIds().stream()
				.map(FieldValue::of)
				.toList();

			boolBuilder.filter(f -> f
				.terms(t -> t
					.field("categoryId")
					.terms(tv -> tv.value(categoryValues))
				)
			);
		}

		// 4. 가격 범위 필터
		if (!ObjectUtils.isEmpty(request.minPrice()) || !ObjectUtils.isEmpty(request.maxPrice())) {
			boolBuilder.filter(f -> f
				.range(r -> {
					RangeQuery.Builder rangeQuery = r.field("price");

					if (!ObjectUtils.isEmpty(request.minPrice())) {
						rangeQuery.gte(JsonData.of(request.minPrice()));
					}

					if (!ObjectUtils.isEmpty(request.maxPrice())) {
						rangeQuery.lte(JsonData.of(request.maxPrice()));
					}

					return rangeQuery;
				})
			);
		}

		// 5. 판매자 필터
		if (StringUtils.hasText(request.sellerCode())) {
			applyExactlyBuilder(boolBuilder, "sellerCode", FieldValue.of(request.sellerCode()));
		}

		// 6. 상품 상태 필터
		if (StringUtils.hasText(request.productStatus())) {
			applyExactlyBuilder(boolBuilder, "productStatus", FieldValue.of(request.productStatus()));
		}

		return Query.of(q -> q.bool(boolBuilder.build()));
	}

	private void addSort(NativeQueryBuilder queryBuilder, String sortBy, String sortDirection) {

		SortOrder order = "asc".equalsIgnoreCase(sortDirection)
			? SortOrder.Asc
			: SortOrder.Desc;

		switch (sortBy) {
			case "price" -> queryBuilder.withSort(s -> s
				.field(f -> f
					.field("price")
					.order(order)
				)
			);

			case "title" -> queryBuilder.withSort(s -> s
				.field(f -> f
					.field("title.keyword")
					.order(order)
				)
			);

			case "updatedAt" -> queryBuilder.withSort(s -> s
				.field(f -> f
					.field("updatedAt")
					.order(order)
				)
			);

			case "createdAt" -> queryBuilder.withSort(s -> s
				.field(f -> f
					.field("createdAt")
					.order(order)
				)
			);

			default -> queryBuilder.withSort(s -> s
				.field(f -> f
					.field("createdAt")
					.order(SortOrder.Desc)
				)
			);
		}
	}

	// 자동 완성 (Prefix와 정확히 일치)
	private ProductSuggestResponse completionSuggest(ProductSuggestRequest request) {

		String keyword = request.keyword().trim();

		if (keyword.isEmpty()) {
			return ProductSuggestResponse.builder()
				.originalKeyword(keyword)
				.type(SuggestType.COMPLETION)
				.suggestions(Collections.emptyList())
				.build();
		}

		BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

		// 1. 삭제되지 않은 상품
		applyExactlyBuilder(boolBuilder, "deleteStatus", FieldValue.of(false));

		// 2. 판매 완료 상품 제외
		if (!Boolean.TRUE.equals(request.includeSold())) {
			boolBuilder.filter(f -> f
				.bool(b -> b
					.mustNot(mn -> mn
						.term(t -> t
							.field("productStatus")
							.value(FieldValue.of("SOLD"))
						)
					)
				)
			);
		}

		if (request.categoryId() != null) {
			applyExactlyBuilder(boolBuilder, "categoryId", FieldValue.of(request.categoryId()));
		}

		boolBuilder.should(s -> s
			.prefix(p -> p
				.field("title.keyword")
				.value(keyword)
			)
		);

		boolBuilder.should(s -> s
			.prefix(p -> p
				.field("categoryName")
				.value(keyword)
			)
		);

		boolBuilder.minimumShouldMatch("1");

		NativeQuery query = NativeQuery.builder()
			.withQuery(Query.of(q -> q.bool(boolBuilder.build())))
			.withPageable(PageRequest.of(0, request.size() * 3))
			.build();

		SearchHits<ProductDocument> searchHits = operations.search(query, ProductDocument.class);

		Set<String> uniqueTitles = new LinkedHashSet<>();
		List<SuggestOption> suggestOptions = new ArrayList<>();

		if (!searchHits.getSearchHits().isEmpty()) {
			for (SearchHit<ProductDocument> searchHit : searchHits.getSearchHits()) {

				ProductDocument document = searchHit.getContent();

				if (!ObjectUtils.isEmpty(document)) {

					String suggestedText = document.getTitle();

					if (!uniqueTitles.add(suggestedText)) {
						continue;
					}

					if (isValidSuggestion(suggestedText, request)) {
						float score = searchHit.getScore();

						suggestOptions.add(SuggestOption.builder()
							.text(suggestedText)
							.score(score)
							.build());

						if (suggestOptions.size() >= request.size()) {
							break;
						}

						log.info("자동 완성 키워드 추가 - Text: {}, Score: {}", suggestedText, score);
					}
				}
			}
		}

		log.info("자동 완성 최종 제안 - Keyword: {}, CategoryId: {}, Size: {}개",
			keyword, request.categoryId(), suggestOptions.size());

		return ProductSuggestResponse.builder()
			.originalKeyword(keyword)
			.type(SuggestType.COMPLETION)
			.suggestions(suggestOptions)
			.build();
	}

	// 연관 검색어
	private ProductSuggestResponse relatedTermSuggest(ProductSuggestRequest request) {

		String keyword = request.keyword();

		BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

		// 1. 키워드 매치(넓은 범위로)
		boolBuilder.should(s -> s
			.match(m -> m
				.field("title")
				.query(keyword)
				.boost(3.0f)
			)
		);

		boolBuilder.should(s -> s
			.match(m -> m
				.field("categoryName")
				.query(keyword)
				.boost(2.0f)
			)
		);

		boolBuilder.should(s -> s
			.match(m -> m
				.field("description")
				.query(keyword)
				.boost(1.0f)
			)
		);

		boolBuilder.minimumShouldMatch("1");

		// 2. 삭제된 상품 제외
		applyExactlyBuilder(boolBuilder, "deleteStatus", FieldValue.of(false));

		// 3. 판매 완료 상품 제외
		if (!request.includeSold()) {
			boolBuilder.filter(f -> f
				.bool(b -> b
					.mustNot(mn -> mn
						.term(t -> t
							.field("productStatus")
							.value(FieldValue.of("SOLD"))
						)
					)
				)
			);
		}

		NativeQuery query = NativeQuery.builder()
			.withQuery(Query.of(q -> q.bool(boolBuilder.build())))
			.withMaxResults(Math.min(request.size() * 10, 100))
			.build();

		SearchHits<ProductDocument> searchHits = operations.search(query, ProductDocument.class);

		// 제목에서 키워드 제거 후 나머지 단어들을 연관 검색어로 추출
		Map<String, RelatedTermInfo> relatedTerms = new HashMap<>();

		for (SearchHit<ProductDocument> searchHit : searchHits.getSearchHits()) {
			String title = searchHit.getContent().getTitle();

			extractRelatedTerms(title, keyword, relatedTerms, searchHit.getScore());
		}

		// 점수 -> 빈도수 기반 정렬
		List<SuggestOption> suggestOptions = relatedTerms.entrySet().stream()
			.sorted((a, b) -> {
				// 빈도수 내림차순
				int countCompare = Long.compare(b.getValue().count, a.getValue().count);

				if (countCompare != 0) {
					return countCompare;
				}

				// 평균 점수 내림차순
				return Float.compare(b.getValue().avgScore, a.getValue().avgScore);
			})
			.limit(request.size())
			.map(entry -> SuggestOption.builder()
				.text(entry.getKey())
				.productCount(entry.getValue().count)
				.score(entry.getValue().avgScore)
				.build()
			)
			.toList();

		log.info("키워드의 연관 검색어 제안 - Suggestions: {}", suggestOptions.size());

		return ProductSuggestResponse.builder()
			.originalKeyword(keyword)
			.type(SuggestType.RELATED)
			.suggestions(suggestOptions)
			.build();
	}

	private void extractRelatedTerms(
		String title, String keyword, Map<String, RelatedTermInfo> relatedTerms, float score
	) {

		String[] words = title.split("\\s+");

		for (String word : words) {
			// 1. 키워드와 같으면 제외
			if (word.equalsIgnoreCase(keyword)) {
				continue;
			}

			// 2. 키워드가 포함됐으면 제외
			if (word.toLowerCase().contains(keyword.toLowerCase())) {
				continue;
			}

			// 3. 너무 짧은 단어 제외
			if (word.length() <= 1) {
				continue;
			}

			// 4. 숫자뿐인 단어 제외
			if (word.matches("\\d+")) {
				continue;
			}

			relatedTerms.compute(word, (k, v) -> {
				if (v == null) {
					return new RelatedTermInfo(1L, score);
				} else {
					return new RelatedTermInfo(v.count + 1, (v.avgScore * v.count + score) / (v.count + 1));
				}
			});
		}
	}

	// 삭제 상품 제외 및 판매완료 상품 제외되었는지 확인
	private boolean isValidSuggestion(String suggestedText, ProductSuggestRequest request) {

		BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

		boolBuilder.must(m -> m
			.term(t -> t
				.field("title.keyword")
				.value(FieldValue.of(suggestedText))
			)
		);

		applyExactlyBuilder(boolBuilder, "deleteStatus", FieldValue.of(false));

		if (!request.includeSold()) {
			boolBuilder.filter(f -> f
				.bool(b -> b
					.mustNot(mn -> mn
						.term(t -> t
							.field("productStatus")
							.value(FieldValue.of("SOLD"))
						)
					)
				)
			);
		}

		NativeQuery query = NativeQuery.builder()
			.withQuery(Query.of(q -> q.bool(boolBuilder.build())))
			.withMaxResults(1)
			.build();

		SearchHits<ProductDocument> searchHits = operations.search(query, ProductDocument.class);

		return searchHits.getTotalHits() > 0;
	}

	// 판매 여부에 따른 상품 제외
	private void applyExactlyBuilder(BoolQuery.Builder boolBuilder, String field, FieldValue fieldValue) {
		boolBuilder.filter(f -> f
			.term(t -> t
				.field(field)
				.value(fieldValue)
			)
		);
	}

	private record RelatedTermInfo(
		long count,
		float avgScore
	) {
	}
}
