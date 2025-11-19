package io.devground.dbay.domain.product.product.util.elasticsearch;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.elasticsearch.core.suggest.Completion;

import io.devground.core.model.vo.DeleteStatus;
import io.devground.dbay.domain.product.category.model.entity.Category;
import io.devground.dbay.domain.product.product.model.entity.Product;
import io.devground.dbay.domain.product.product.model.entity.ProductDocument;
import io.devground.dbay.domain.product.product.model.entity.ProductSale;
import io.devground.dbay.domain.product.product.model.vo.ProductStatus;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductESUtil {

	public ProductDocument toProductDocument(Product product) {

		ProductSale productSale = product.getProductSale();
		Category category = product.getCategory();

		List<String> suggestInputs = buildSuggestInputs(product);
		int weight = calculateSuggestWeight(product);

		Completion suggest = new Completion(suggestInputs.toArray(new String[0]));
		suggest.setWeight(weight);

		return ProductDocument.builder()
			.id(product.getId())
			.productCode(product.getCode())
			.title(product.getTitle())
			.description(product.getDescription())
			.thumbnailUrl(product.getThumbnailUrl())
			.categoryId(category.getId())
			.categoryName(category.getName())
			.categoryFullPath(category.getFullPath())
			.sellerCode(productSale != null ? productSale.getSellerCode() : null)
			.price(productSale != null ? productSale.getPrice() : null)
			.productStatus(productSale != null ? productSale.getProductStatus().name() : null)
			.createdAt(product.getCreatedAt().toLocalDate())
			.updatedAt(product.getUpdatedAt().toLocalDate())
			.deleteStatus(product.getDeleteStatus().equals(DeleteStatus.Y))
			.suggest(suggest)
			.build();
	}

	// Suggest용 Input을 생성
	public List<String> buildSuggestInputs(Product product) {

		Set<String> inputs = new LinkedHashSet<>();
		String title = product.getTitle();

		inputs.add(title);

		String[] words = title.split("\\s+");
		for (String word : words) {
			if (word.length() >= 2) {
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

		if (productSale != null && productSale.getPrice() != null) {
			long price = productSale.getPrice();
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

		if (productSale != null && ProductStatus.ON_SALE.equals(productSale.getProductStatus())) {
			weight += 20;
		}

		return weight;
	}
}
