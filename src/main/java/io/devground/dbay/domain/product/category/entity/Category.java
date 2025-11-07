package io.devground.dbay.domain.product.category.entity;

import java.util.ArrayList;
import java.util.List;

import io.devground.core.model.entity.BaseEntity;
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

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentId")
	private Category parentCategory;

	@OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Category> children = new ArrayList<>();

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Integer depth = 1;

	@Builder
	public Category(Category parent, String name, Integer depth) {
		this.parentCategory = parent;
		this.name = name;
		this.depth = depth != null ? depth : 1;
	}

	public void addChildren(Category children) {
		this.children.add(children);
		children.parentCategory = this;
	}

	public void removeChildren(Category children) {
		this.children.remove(children);
		children.parentCategory = null;
	}
}
