package io.devground.product.product.infrastructure.model.persistence;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.core.suggest.Completion;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@Document(indexName = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setting(settingPath = "elasticsearch/product-settings.json")
public class ProductDocument {

	@Id
	private Long id;

	@Field(type = FieldType.Keyword)
	private String productCode;

	@MultiField(
		mainField = @Field(type = FieldType.Text, analyzer = "nori_synonym_analyzer", searchAnalyzer = "nori_synonym_analyzer"),
		otherFields = {
			@InnerField(suffix = "keyword", type = FieldType.Keyword, normalizer = "lowercase_normalizer"),
			@InnerField(suffix = "completion", type = FieldType.Search_As_You_Type, maxShingleSize = 3),
			@InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "edge_ngram_analyzer", searchAnalyzer = "nori_synonym_analyzer"),
			@InnerField(suffix = "shingle", type = FieldType.Text, analyzer = "shingle_analyzer", searchAnalyzer = "nori_synonym_analyzer")
		}
	)
	private String title;

	@MultiField(
		mainField = @Field(type = FieldType.Text, analyzer = "nori_synonym_analyzer", searchAnalyzer = "nori_synonym_analyzer"),
		otherFields = {
			@InnerField(suffix = "keyword", type = FieldType.Keyword, normalizer = "lowercase_normalizer"),
			@InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "edge_ngram_analyzer", searchAnalyzer = "nori_synonym_analyzer")
		}
	)
	private String description;

	@Field(type = FieldType.Keyword)
	private String thumbnailUrl;

	@Field(type = FieldType.Long)
	private Long categoryId;

	@Field(type = FieldType.Keyword)
	private String categoryName;

	@MultiField(
		mainField = @Field(type = FieldType.Text, analyzer = "nori_synonym_analyzer", searchAnalyzer = "nori_synonym_analyzer"),
		otherFields = {
			@InnerField(suffix = "keyword", type = FieldType.Keyword),
			@InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "edge_ngram_analyzer")
		}
	)
	private String categoryFullPath;

	@Field(type = FieldType.Long)
	private List<Long> categoryPathIds;

	@Field(type = FieldType.Keyword)
	private String sellerCode;

	@Field(type = FieldType.Long)
	private Long price;

	@Field(type = FieldType.Keyword)
	private String productStatus;

	@Field(type = FieldType.Date)
	private LocalDate createdAt;

	@Field(type = FieldType.Date)
	private LocalDate updatedAt;

	@Field(type = FieldType.Boolean)
	private Boolean deleteStatus;

	@CompletionField
	private Completion suggest;
}
