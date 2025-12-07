package io.devground.product.domain.model;

import java.time.LocalDateTime;

import io.devground.core.model.vo.DeleteStatus;
import io.devground.core.util.CodeUtil;
import io.devground.product.domain.vo.ProductSpec;

public class Product {

	private Long id;
	private String code;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private ProductSpec productSpec;

	private String thumbnailUrl;

	private Category category;

	private ProductSale productSale;

	private DeleteStatus deleteStatus;

	public Product(ProductSpec productSpec, Category category) {

		this.code = CodeUtil.generateUUID();

		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();

		this.productSpec = productSpec;

		this.category = category;
	}

	public void linkProductSale(ProductSale productSale) {
		this.productSale = productSale;
	}

	public void updateSpec(ProductSpec productSpec) {
		this.productSpec = productSpec;
	}

	public void updateId(Long id) {
		this.id = id;
	}

	public void updateClock() {
		this.updatedAt = LocalDateTime.now();
	}

	public void updateDeleteStatus(DeleteStatus deleteStatus) {
		this.deleteStatus = deleteStatus;
		this.updateClock();
	}

	public void updateThumbnail(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public Long getId() {
		return id;
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

	public ProductSpec getProductSpec() {
		return productSpec;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public Category getCategory() {
		return category;
	}

	public ProductSale getProductSale() {
		return productSale;
	}

	public DeleteStatus getDeleteStatus() {
		return deleteStatus;
	}
}
