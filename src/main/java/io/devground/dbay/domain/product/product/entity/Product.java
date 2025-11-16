package io.devground.dbay.domain.product.product.entity;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.product.category.entity.Category;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	indexes = {
		@Index(name = "idx_product_delete_status_created", columnList = "deleteStatus, createdAt"),
		@Index(name = "idx_product_code_deleted", columnList = "code, deleteStatus")
	}
)
public class Product extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Lob
	@Column(nullable = false)
	private String description;

	private String thumbnailUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoryId", nullable = false)
	private Category category;

	@OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private ProductSale productSale;

	@Builder
	public Product(Category category, String title, String description) {
		validateCategory(category);
		this.category = category;
		this.title = title;
		this.description = description;
		this.thumbnailUrl = "";
	}

	public void addProductSale(ProductSale productSale) {
		this.productSale = productSale;
	}

	public void changeProductMetadata(String title, String description) {
		this.title = title;
		this.description = description;
	}

	public void updateThumbnail(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	private void validateCategory(Category category) {
		if (category != null && !category.isLeaf()) {
			ErrorCode.PRODUCT_MUST_WITH_LEAF_CATEGORY.throwServiceException();
		}
	}
}
