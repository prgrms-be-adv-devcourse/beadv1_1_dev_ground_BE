package io.devground.dbay.domain.product.category.model.entity;

import java.util.ArrayList;
import java.util.List;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.vo.ErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

	private static final int MAX_DEPTH = 5;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentId")
	private Category parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Category> children = new ArrayList<>();

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Integer depth = 1;

	@Column(nullable = false)
	private String fullPath;

	@Builder
	public Category(Category parent, String name, Integer depth) {
		this.validateDepth(parent, depth);
		this.parent = parent;
		this.name = name;
		this.depth = depth;
		this.fullPath = calculateFullPath(parent, name);
	}

	public void addChildren(Category children) {
		this.children.add(children);
		children.parent = this;
	}

	public void removeChildren(Category children) {
		this.children.remove(children);
		children.parent = null;
	}

	public boolean isLeaf() {
		return this.children.isEmpty();
	}

	private void validateDepth(Category parent, Integer depth) {
		int calculatedDepth = depth != null ? depth : 1;

		if (calculatedDepth > MAX_DEPTH) {
			ErrorCode.CANNOT_EXCEED_MAX_DEPTH.throwServiceException();
		}

		if (parent != null && parent.getDepth() + 1 != calculatedDepth) {
			ErrorCode.MISMATCH_ON_DEPTH.throwServiceException();
		}
	}

	private String calculateFullPath(Category parent, String name) {
		if (parent == null) {
			return name;
		}

		return parent.getFullPath() + "/" + name;
	}
}
