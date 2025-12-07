package io.devground.image.domain.model;

import java.time.LocalDateTime;

import io.devground.core.util.CodeUtil;
import io.devground.image.domain.vo.ImageSpec;

public class Image {

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

	public void updateClock() {
		this.updatedAt = LocalDateTime.now();
	}
}
