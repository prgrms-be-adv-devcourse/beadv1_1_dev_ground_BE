package io.devground.product.product.infrastructure.model.persistence;

import java.util.ArrayList;
import java.util.List;

import io.devground.core.model.entity.BaseEntity;
import io.devground.product.product.domain.vo.DomainErrorCode;
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
public class CategoryEntity extends BaseEntity {

	private static final int MAX_DEPTH = 5;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentId")
	private CategoryEntity parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CategoryEntity> children = new ArrayList<>();

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Integer depth = 1;

	@Column(nullable = false)
	private String fullPath;

	@Builder
	public CategoryEntity(String code, CategoryEntity parent, String name, Integer depth) {
		this.registCode(code);

		this.validateDepth(parent, depth);
		this.parent = parent;
		this.name = name;
		this.depth = depth;
		this.fullPath = calculateFullPath(parent, name);
	}

	public void addChildren(CategoryEntity children) {
		this.children.add(children);
		children.parent = this;
	}

	public void removeChildren(CategoryEntity children) {
		this.children.remove(children);
		children.parent = null;
	}

	public boolean isLeaf() {
		return this.children.isEmpty();
	}

	private void validateDepth(CategoryEntity parent, Integer depth) {
		int calculatedDepth = depth != null ? depth : 1;

		if (calculatedDepth > MAX_DEPTH) {
			DomainErrorCode.CANNOT_EXCEED_MAX_DEPTH.throwException();
		}

		if (parent != null && parent.getDepth() + 1 != calculatedDepth) {
			DomainErrorCode.MISMATCH_ON_DEPTH.throwException();
		}
	}

	private String calculateFullPath(CategoryEntity parent, String name) {
		if (parent == null) {
			return name;
		}

		return parent.getFullPath() + "/" + name;
	}
}
