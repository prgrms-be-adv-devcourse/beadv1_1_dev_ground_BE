package io.devground.product.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.devground.product.domain.util.DomainUtils;
import io.devground.product.domain.vo.DomainErrorCode;

public class Category {

	private static final int MAX_DEPTH = 3;

	private String code;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private String parentCode;

	private List<Category> children;

	private String name;
	private Integer depth;
	private String fullPath;

	public Category(String parentCode, Integer parentDepth, String name, String parentFullPath) {
		int calculatedDepth = (parentDepth == null) ? 1 : parentDepth + 1;

		this.validate(calculatedDepth);

		this.code = DomainUtils.generateCode();

		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();

		this.parentCode = parentCode;
		this.children = new ArrayList<>();

		this.name = name;
		this.depth = calculatedDepth;

		this.fullPath = this.calculateFullPath(parentFullPath, name);
	}

	public void updateClock() {
		this.updatedAt = LocalDateTime.now();
	}

	public void linkChildren(List<Category> children) {
		this.children = children;
	}

	public String calculateFullPath(String parentFullPath, String name) {
		if (parentFullPath == null || parentFullPath.isBlank()) {
			return name;
		}

		return parentFullPath + "/" + name;
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

	public String getParentCode() {
		return parentCode;
	}

	public List<Category> getChildren() {
		return children;
	}

	public String getName() {
		return name;
	}

	public Integer getDepth() {
		return depth;
	}

	public String getFullPath() {
		return fullPath;
	}

	public boolean isLeaf() {
		return this.children.isEmpty();
	}

	private void validate(int calculatedDepth) {
		if (calculatedDepth > MAX_DEPTH) {
			DomainErrorCode.CANNOT_EXCEED_MAX_DEPTH.throwException();
		}
	}
}
