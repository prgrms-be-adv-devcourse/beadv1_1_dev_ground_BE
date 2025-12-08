package io.devground.product.product.infrastructure.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.elasticsearch.core.suggest.Completion;

import io.devground.core.model.vo.DeleteStatus;
import io.devground.product.product.domain.model.Category;
import io.devground.product.product.domain.model.Product;
import io.devground.product.product.domain.model.ProductSale;
import io.devground.product.product.domain.vo.ProductSaleSpec;
import io.devground.product.product.domain.vo.ProductSpec;
import io.devground.product.product.domain.vo.ProductStatus;
import io.devground.product.product.infrastructure.model.persistence.ProductDocument;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductESUtil {

	public ProductDocument toProductDocument(Product product) {

		ProductSpec productSpec = product.getProductSpec();
		ProductSale productSale = product.getProductSale();
		ProductSaleSpec productSaleSpec = productSale.getProductSaleSpec();

		Category category = product.getCategory();

		List<String> suggestInputs = buildSuggestInputs(product);
		int weight = calculateSuggestWeight(product);

		Completion suggest = new Completion(suggestInputs.toArray(new String[0]));
		suggest.setWeight(weight);

		return ProductDocument.builder()
			.id(product.getId())
			.productCode(product.getCode())
			.title(productSpec.title())
			.description(productSpec.description())
			.thumbnailUrl(product.getThumbnailUrl())
			.categoryId(category.getId())
			.categoryName(category.getName())
			.categoryFullPath(category.getFullPath())
			.sellerCode(productSale != null ? productSale.getSellerCode() : null)
			.price(productSaleSpec != null ? productSaleSpec.price() : null)
			.productStatus(productSale != null ? productSaleSpec.productStatus().name() : null)
			.createdAt(product.getCreatedAt().toLocalDate())
			.updatedAt(product.getUpdatedAt().toLocalDate())
			.deleteStatus(product.getDeleteStatus().equals(DeleteStatus.Y))
			.suggest(suggest)
			.build();
	}

	// Suggest용 Input을 생성
	public List<String> buildSuggestInputs(Product product) {

		Set<String> inputs = new LinkedHashSet<>();
		String title = product.getProductSpec().title();

		inputs.add(title);

		String[] words = title.split("\\s+");
		for (String word : words) {
			if (!word.isEmpty()) {
				inputs.add(word);
			}
		}

		for (int i = 0; i < words.length - 1; i++) {
			inputs.add(words[i] + " " + words[i + 1]);
		}

		return new ArrayList<>(inputs);
	}

	// Suggest 가중치 계산
	public int calculateSuggestWeight(Product product) {

		int weight = 1;

		if (product.getCreatedAt() != null) {
			long gapDays = ChronoUnit.DAYS.between(product.getCreatedAt(), LocalDateTime.now());

			if (gapDays <= 7) {
				weight += 30;
			}
		}

		ProductSale productSale = product.getProductSale();

		if (productSale != null && productSale.getProductSaleSpec().price() != null) {
			long price = productSale.getProductSaleSpec().price();
			if (price >= 500000) {
				weight += 50;
			} else if (price >= 300000) {
				weight += 30;
			} else if (price >= 100000) {
				weight += 10;
			} else {
				weight += 5;
			}
		}

		if (productSale != null && ProductStatus.ON_SALE.equals(productSale.getProductSaleSpec().productStatus())) {
			weight += 20;
		}

		return weight;
	}
}
