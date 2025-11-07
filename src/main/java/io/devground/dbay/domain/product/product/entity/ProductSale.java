package io.devground.dbay.domain.product.product.entity;

import java.time.LocalDateTime;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.product.product.vo.ProductStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductSale extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "productId", nullable = false, unique = true)
	private Product product;

	@Column(nullable = false)
	private String sellerCode;

	@Column(nullable = false)
	private Long price;

	@Enumerated(EnumType.STRING)
	private ProductStatus productStatus;

	private LocalDateTime soldAt;

	@Builder
	public ProductSale(String sellerCode, Long price, Product product) {
		this.sellerCode = sellerCode;
		this.price = price;
		this.product = product;
		this.productStatus = ProductStatus.ON_SALE;
	}

	public void addProduct(Product product) {
		this.product = product;
		product.addProductSale(this);
	}

	public void changeAsSold() {
		if (isSold()) {
			throw ErrorCode.ONLY_ON_SALE_PRODUCT_CHANGEABLE.throwServiceException();
		}

		this.productStatus = ProductStatus.SOLD;
		this.soldAt = LocalDateTime.now();
	}

	public void changePrice(Long price) {
		if (isSold()) {
			throw ErrorCode.SOLD_PRODUCT_CANNOT_UPDATE.throwServiceException();
		}

		this.price = price;
	}

	public boolean isSold() {
		return this.productStatus == ProductStatus.ON_SALE;
	}
}
