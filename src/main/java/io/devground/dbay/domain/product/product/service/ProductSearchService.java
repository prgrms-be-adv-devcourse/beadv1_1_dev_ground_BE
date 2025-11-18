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
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.SuggestMode;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.Suggester;
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
			log.info("키워드 제안 - keyword={}, type={}", keyword, request.type());

			return switch (request.type()) {
				case COMPLETION -> completionSuggest(request);
				case PHRASE -> phraseSuggest(request);
				case RELATED -> relatedTermSuggest(request);
			};
		} catch (Exception e) {
			log.error("키워드 추천 실패");

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
		boolBuilder.filter(f -> f
			.term(t -> t
				.field("deleteStatus")
				.value(FieldValue.of(false))
			)
		);

		// 2. 키워드 검색 (title, description) (가중치 계산)
		if (StringUtils.hasText(request.keyword())) {
			boolBuilder.must(m -> m
				.multiMatch(mm -> mm
					.query(request.keyword())
					.fields(
						"title^3",
						"title.ngram^2",
						"title.shingle^2",
						"description",
						"description.ngram",
						"categoryName^1.5",
						"categoryFullPath"
					)
					.type(TextQueryType.BestFields)
					.fuzziness("AUTO")
					.prefixLength(1)
				)
			);
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
			boolBuilder.filter(f -> f
				.term(t -> t
					.field("sellerCode")
					.value(FieldValue.of(request.sellerCode()))
				)
			);
		}

		// 6. 상품 상태 필터
		if (StringUtils.hasText(request.productStatus())) {
			boolBuilder.filter(f -> f
				.term(t -> t
					.field("productStatus")
					.value(FieldValue.of(request.productStatus()))
				)
			);
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

	// 자동 완성
	private ProductSuggestResponse completionSuggest(ProductSuggestRequest request) {

		String keyword = request.keyword();

		NativeQuery query = NativeQuery.builder()
			.withQuery(q -> q
				.multiMatch(m -> m
					.query(keyword)
					.fields("title.completion")
					.type(TextQueryType.BoolPrefix)
				)
			)
			.withMaxResults(request.size())
			.build();

		SearchHits<ProductDocument> searchHits = operations.search(query, ProductDocument.class);

		Set<String> uniqueTitles = new LinkedHashSet<>();
		List<SuggestOption> suggestOptions = new ArrayList<>();

		for (SearchHit<ProductDocument> hit : searchHits.getSearchHits()) {
			String title = hit.getContent().getTitle();

			if (uniqueTitles.add(title)) {
				suggestOptions.add(SuggestOption.builder()
					.text(title)
					.score(hit.getScore())
					.build()
				);
			}

			if (suggestOptions.size() >= request.size()) {
				break;
			}
		}

		log.info("자동 완성 키워드 제안 - Suggestions: {}", suggestOptions.size());

		return ProductSuggestResponse.builder()
			.originalKeyword(keyword)
			.type(SuggestType.COMPLETION)
			.suggestions(suggestOptions)
			.build();
	}

	// 오타 수정
	private ProductSuggestResponse phraseSuggest(ProductSuggestRequest request) {

		String keyword = request.keyword();

		FieldSuggester fieldSuggester = FieldSuggester.of(fs -> fs
			.text(keyword)
			.phrase(p -> p
				.field("title")
				.size(request.size())
				.gramSize(3)
				.maxErrors(2.0)
				.confidence(0.0)
				.directGenerator(dg -> dg
					.field("title")
					.suggestMode(SuggestMode.Always)
					.minWordLength(2)
					.maxEdits(2)
				)
			)
		);

		Suggester suggester = Suggester.of(s -> s.suggesters("phrase-suggest", fieldSuggester));

		NativeQuery query = NativeQuery.builder()
			.withSuggester(suggester)
			.build();

		SearchHits<ProductDocument> searchHits = operations.search(query, ProductDocument.class);

		List<SuggestOption> suggestOptions = new ArrayList<>();

		if (searchHits.hasSuggest()) {
			Suggest suggest = searchHits.getSuggest();

			if (suggest != null) {
				suggest.getSuggestion("phrase-suggest")
					.getEntries()
					.forEach(entry -> entry
						.getOptions()
						.forEach(option ->
							suggestOptions.add(SuggestOption.builder()
								.text(option.getText())
								.score(option.getScore() != null ? option.getScore().floatValue() : 0)
								.build())
						)
					);
			}
		}

		log.info("오타로 가정 후 키워드 제안 - Suggestions: {}", suggestOptions.size());

		return ProductSuggestResponse.builder()
			.originalKeyword(keyword)
			.type(SuggestType.PHRASE)
			.suggestions(suggestOptions)
			.build();
	}

	// 연관 검색어
	private ProductSuggestResponse relatedTermSuggest(ProductSuggestRequest request) {

		String keyword = request.keyword();

		NativeQuery query = NativeQuery.builder()
			.withQuery(q -> q
				.bool(b -> b
					.should(s -> s
						.match(m -> m
							.field("title")
							.query(keyword)
						)
					)
					.should(s -> s
						.match(m -> m
							.field("title.ngram")
							.query(keyword)
						)
					)
				)
			)
			.withMaxResults(request.size())
			.build();

		SearchHits<ProductDocument> searchHits = operations.search(query, ProductDocument.class);

		Map<String, Long> termFrequency = new HashMap<>();

		for (SearchHit<ProductDocument> searchHit : searchHits.getSearchHits()) {
			String title = searchHit.getContent().getTitle();

			if (title.toLowerCase().contains(keyword.toLowerCase())) {
				termFrequency.merge(title, 1L, Long::sum);
			}
		}

		List<SuggestOption> suggestOptions = termFrequency.entrySet().stream()
			.sorted(Map.Entry.<String, Long>comparingByValue().reversed())
			.limit(request.size())
			.map(entry -> SuggestOption.builder()
				.text(entry.getKey())
				.productCount(entry.getValue())
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
}
