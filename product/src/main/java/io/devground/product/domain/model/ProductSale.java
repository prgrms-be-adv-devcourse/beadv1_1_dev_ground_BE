package io.devground.product.domain.model;

import java.time.LocalDateTime;

import io.devground.product.domain.util.DomainUtils;
import io.devground.product.domain.vo.DomainErrorCode;
import io.devground.product.domain.vo.ProductSaleSpec;

public class ProductSale {

	private String code;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private String sellerCode;

	private ProductSaleSpec productSaleSpec;

	private LocalDateTime soldAt;

	public ProductSale(String sellerCode, ProductSaleSpec productSaleSpec) {
		validate(sellerCode);

		this.code = DomainUtils.generateCode();

		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();

		this.sellerCode = sellerCode;
		this.productSaleSpec = productSaleSpec;
	}

	public void updateSoldComplete() {
		this.soldAt = LocalDateTime.now();
	}

	public void updateClock() {
		this.updatedAt = LocalDateTime.now();
	}

	private void validate(String sellerCode) {
		if (sellerCode == null || sellerCode.isBlank()) {
			DomainErrorCode.SELLER_CODE_MUST_BE_INPUT.throwException();
		}
	}

	public String getCode() {
		return code;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public String getSellerCode() {
		return sellerCode;
	}

	public ProductSaleSpec getProductSaleSpec() {
		return productSaleSpec;
	}

	public LocalDateTime getSoldAt() {
		return soldAt;
	}
}
