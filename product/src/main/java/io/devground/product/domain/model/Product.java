package io.devground.product.domain.model;

import java.time.LocalDateTime;

import io.devground.product.domain.util.DomainUtil;
import io.devground.product.domain.vo.DeleteStatus;
import io.devground.product.domain.vo.ProductSpec;

public class Product {

	private String code;
	private DeleteStatus deleteStatus;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private ProductSpec productSpec;

	private Category category;

	private ProductSale productSale;

	public Product(ProductSpec productSpec, Category category) {

		this.code = DomainUtil.generateCode();
		this.deleteStatus = DeleteStatus.N;

		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();

		this.productSpec = productSpec;

		this.category = category;
	}

	public void linkProductSale(ProductSale productSale) {
		this.productSale = productSale;
	}

	public void updateClock() {
		this.updatedAt = LocalDateTime.now();
	}

	public String getCode() {
		return code;
	}

	public DeleteStatus getDeleteStatus() {
		return deleteStatus;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public ProductSpec getProductSpec() {
		return productSpec;
	}

	public Category getCategory() {
		return category;
	}

	public ProductSale getProductSale() {
		return productSale;
	}
}
