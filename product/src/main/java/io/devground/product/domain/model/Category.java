package io.devground.product.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import io.devground.product.domain.util.DomainUtil;
import io.devground.product.domain.vo.DeleteStatus;
import io.devground.product.domain.vo.DomainErrorCode;

public class Category {

	private static final int MAX_DEPTH = 3;

	private String code;
	private DeleteStatus deleteStatus;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private Category parent;

	private List<Category> children;

	private String name;
	private Integer depth;
	private String fullPath;

	public Category(Category parent, String name, Integer depth) {
		this.validate(parent, depth);

		this.code = DomainUtil.generateCode();
		this.deleteStatus = DeleteStatus.N;

		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();

		this.parent = parent;
		this.children = List.of();

		this.name = name;
		this.depth = depth;

		this.fullPath = this.calculateFullPath(parent, name);
	}

	public void updateClock() {
		this.updatedAt = LocalDateTime.now();
	}

	public String calculateFullPath(Category parent, String name) {
		if (parent == null) {
			return name;
		}

		return parent.getFullPath() + "/" + name;
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

	public boolean isLeaf() {
		return this.children.isEmpty();
	}

	private void validate(Category parent, Integer depth) {
		int calculatedDepth = depth != null ? depth : 1;

		if (calculatedDepth > MAX_DEPTH) {
			DomainErrorCode.CANNOT_EXCEED_MAX_DEPTH.throwException();
		}

		if (parent != null && parent.getDepth() + 1 != calculatedDepth) {
			DomainErrorCode.MISMATCH_ON_DEPTH.throwException();
		}
	}
}
