package io.devground.product.product.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.devground.core.util.CodeUtil;
import io.devground.product.product.domain.vo.ProductDomainErrorCode;

public class Category {

	private static final int MAX_DEPTH = 3;

	private Long id;
	private String code;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private Category parent;
	private List<Category> children;

	private String name;
	private Integer depth;
	private String fullPath;

	public Category() {
		this.children = new ArrayList<>();
	}

	public static Category of(Category parent, String name) {
		Category category = new Category();

		category.code = CodeUtil.generateUUID();

		category.createdAt = LocalDateTime.now();
		category.updatedAt = LocalDateTime.now();

		category.parent = parent;
		category.children = new ArrayList<>();
		category.name = name;

		category.depth = (parent == null) ? 1 : parent.depth + 1;
		category.validate();

		category.fullPath = category.calculateFullPath();

		return category;
	}

	public static Category from(
		Long id, String code, LocalDateTime createdAt, LocalDateTime updatedAt, Category parent, String name,
		Integer depth, String fullPath
	) {
		Category category = new Category();
		category.id = id;
		category.code = code;

		category.createdAt = createdAt;
		category.updatedAt = updatedAt;

		category.parent = parent;
		category.children = new ArrayList<>();
		category.name = name;

		category.depth = depth;
		category.fullPath = fullPath;

		return category;
	}

	public void updateClock() {
		this.updatedAt = LocalDateTime.now();
	}

	public void linkChildren(List<Category> children) {
		this.children = children;
	}

	public void addChild(Category child) {
		this.children.add(child);
		child.parent = this;
	}

	public String calculateFullPath() {
		if (parent == null) {
			return name;
		}

		return parent.fullPath + "/" + name;
	}

	public boolean isLeaf() {
		return this.children.isEmpty();
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

	public Category getParent() {
		return parent;
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

	private void validate() {
		if (depth > MAX_DEPTH) {
			ProductDomainErrorCode.CANNOT_EXCEED_MAX_DEPTH.throwException();
		}

		if (parent != null && parent.depth + 1 != depth) {
			ProductDomainErrorCode.MISMATCH_ON_DEPTH.throwException();
		}
	}
}
