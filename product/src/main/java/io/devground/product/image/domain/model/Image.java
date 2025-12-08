package io.devground.product.image.domain.model;

import java.time.LocalDateTime;

import io.devground.core.util.CodeUtil;
import io.devground.product.image.domain.vo.ImageSpec;

public class Image {

	private Long id;
	private String code;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private ImageSpec imageSpec;

	public Image(ImageSpec imageSpec) {

		this.code = CodeUtil.generateUUID();

		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();

		this.imageSpec = imageSpec;
	}

	public void updateId(Long id) {
		this.id = id;
	}

	public void updateClock() {
		this.updatedAt = LocalDateTime.now();
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

	public ImageSpec getImageSpec() {
		return imageSpec;
	}
}
