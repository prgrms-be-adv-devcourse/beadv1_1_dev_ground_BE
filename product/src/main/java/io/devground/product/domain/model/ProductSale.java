package io.devground.product.domain.model;

import java.time.LocalDateTime;

import io.devground.core.util.CodeUtil;
import io.devground.product.domain.vo.DomainErrorCode;
import io.devground.product.domain.vo.ProductSaleSpec;

public class ProductSale {

	private String code;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private String productCode;
	private String sellerCode;

	private ProductSaleSpec productSaleSpec;

	private LocalDateTime soldAt;

	public ProductSale(String sellerCode, String productCode, ProductSaleSpec productSaleSpec) {
		validate(sellerCode, productCode);

		this.code = CodeUtil.generateUUID();

		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();

		this.productCode = productCode;
		this.sellerCode = sellerCode;
		this.productSaleSpec = productSaleSpec;
	}

	public void updateToSold(ProductSaleSpec productSaleSpec) {
		this.updateSpec(productSaleSpec);
		this.updateSoldComplete();
	}

	public void updateSpec(ProductSaleSpec productSaleSpec) {
		this.productSaleSpec = productSaleSpec;
	}

	public void updateSoldComplete() {
		this.soldAt = LocalDateTime.now();
		this.updateClock();
	}

	public void updateClock() {
		this.updatedAt = LocalDateTime.now();
	}

	private void validate(String sellerCode, String productCode) {
		if (sellerCode == null || sellerCode.isBlank()) {
			DomainErrorCode.SELLER_CODE_MUST_BE_INPUT.throwException();
		}

		if (productCode == null || productCode.isBlank()) {
			DomainErrorCode.PRODUCT_CODE_MUST_BE_INPUT.throwException();
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

	public String getProductCode() {
		return productCode;
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
