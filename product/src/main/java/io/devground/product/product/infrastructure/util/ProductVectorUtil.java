package io.devground.product.product.infrastructure.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;

import io.devground.product.product.domain.model.Product;
import io.devground.product.product.domain.vo.ProductRecommendSpec;
import io.devground.product.product.domain.vo.ProductStatus;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductVectorUtil {

	public Document toVectorDocument(Product product) {

		String content = """
			카테고리: %s
			상품명: %s
			설명: %s
			""".formatted(
			product.getCategory().getFullPath(),
			product.getProductSpec().title(),
			product.getProductSpec().description()
		);

		Map<String, Object> metadata = Map.of(
			"productCode", product.getCode(),
			"categoryId", product.getCategory().getId(),
			"categoryFullPath", product.getCategory().getFullPath(),
			"title", product.getProductSpec().title(),
			"description", product.getProductSpec().description(),
			"thumbnailUrl", product.getThumbnailUrl(),
			"price", product.getProductSale().getProductSaleSpec().price(),
			"productStatus", product.getProductSale().getProductSaleSpec().productStatus().name(),
			"deleteStatus", product.getDeleteStatus().name()
		);

		return new Document(product.getCode(), content, metadata);
	}

	public ProductRecommendSpec toRecommendSpec(Document document) {

		Map<String, Object> metadata = document.getMetadata();

		String productCode = (String) metadata.getOrDefault("productCode", document.getId());
		String title = (String) metadata.getOrDefault("title", "");
		String description = (String) metadata.getOrDefault("description", "");
		String categoryFullPath = (String) metadata.getOrDefault("categoryFullPath", "");
		String thumbnailUrl = (String) metadata.getOrDefault("thumbnailUrl", "");
		String productStatus = metadata.getOrDefault("productStatus", ProductStatus.ON_SALE.name()).toString();

		Object metaPrice = metadata.get("price");
		Long price = 0L;
		if (metaPrice instanceof Number number) {
			price = number.longValue();
		}

		return new ProductRecommendSpec(
			productCode, title, description, price, categoryFullPath, thumbnailUrl, productStatus
		);
	}

	public ProductRecommendSpec toRecommendSpec(Product product) {

		return new ProductRecommendSpec(
			product.getCode(),
			product.getProductSpec().title(),
			product.getProductSpec().description(),
			product.getProductSale().getProductSaleSpec().price(),
			product.getCategory().getFullPath(),
			product.getThumbnailUrl(),
			product.getProductSale().getProductSaleSpec().productStatus().name()
		);
	}

	public String createQueryFromUserViewedProducts(List<Product> products) {

		return products.stream()
			.map(ProductVectorUtil::createQueryFromProductDetail)
			.collect(Collectors.joining("\n"));
	}

	public String createQueryFromProductDetail(Product product) {

		return """
			카테고리: %s
			상품명: %s
			설명: %s
			""".formatted(
			product.getCategory().getFullPath(),
			product.getProductSpec().title(),
			product.getProductSpec().description()
		);
	}
}
