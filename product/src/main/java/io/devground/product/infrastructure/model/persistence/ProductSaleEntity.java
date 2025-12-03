package io.devground.product.infrastructure.model.persistence;

import java.time.LocalDateTime;

import io.devground.core.model.entity.BaseEntity;
import io.devground.product.domain.vo.DomainErrorCode;
import io.devground.product.domain.vo.ProductStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
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
		@Index(
			name = "idx_product_sale_seller_status_created",
			columnList = "sellerCode, productStatus, createdAt"
		)
	}
)
public class ProductSaleEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "productId", nullable = false, unique = true)
	private ProductEntity product;

	@Column(nullable = false)
	private String sellerCode;

	@Column(nullable = false)
	private Long price;

	@Enumerated(EnumType.STRING)
	private ProductStatus productStatus;

	private LocalDateTime soldAt;

	@Builder
	public ProductSaleEntity(String code, String sellerCode, Long price, ProductEntity product) {
		this.registCode(code);

		this.sellerCode = sellerCode;
		this.price = price;
		this.product = product;
		this.productStatus = ProductStatus.ON_SALE;
	}

	public void addProduct(ProductEntity product) {
		this.product = product;
		product.addProductSale(this);
	}

	public void changeAsSold() {
		if (isSold()) {
			DomainErrorCode.ONLY_ON_SALE_PRODUCT_CHANGEABLE.throwException();
		}

		this.productStatus = ProductStatus.SOLD;
		this.soldAt = LocalDateTime.now();
	}

	public void changePrice(Long price) {
		if (isSold()) {
			DomainErrorCode.SOLD_PRODUCT_CANNOT_UPDATE.throwException();
		}

		this.price = price;
	}

	public boolean isSold() {
		return this.productStatus == ProductStatus.SOLD;
	}
}
