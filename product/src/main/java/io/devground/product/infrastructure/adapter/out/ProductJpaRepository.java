package io.devground.product.infrastructure.adapter.out;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.devground.product.domain.model.Product;
import io.devground.product.domain.vo.response.CartProductsResponse;
import io.devground.product.infrastructure.model.persistence.ProductEntity;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

	Optional<ProductEntity> findByCode(String productCode);

	@Query("""
		SELECT new io.devground.product.domain.vo.response.CartProductsResponse
		(
			p.code,
			ps.code,
			ps.sellerCode,
			p.title,
			ps.price
		)
		FROM ProductEntity p
		JOIN p.productSale ps
		WHERE p.code IN :productCodes
		AND ps.productStatus = 'ON_SALE'
		"""
	)
	List<CartProductsResponse> findCartProductsByProductCodes(@Param("productCodes") List<String> productCodes);

	List<ProductEntity> findAllByCodeIn(List<String> codes);

	@Query(
		value = """
			SELECT p
			FROM ProductEntity p
			JOIN FETCH p.productSale ps
			WHERE p.deleteStatus = 'N'
			""",
		countQuery = """
			SELECT count(p)
			From ProductEntity p
			where p.deleteStatus = 'N'
			"""
	)
	Page<ProductEntity> findAllWithSale(Pageable pageable);

	@Query(
		value = """
			SELECT p
			FROM ProductEntity p
			JOIN FETCH p.category c
			JOIN FETCH p.productSale ps
			""",
		countQuery = """
			SELECT count(p)
			FROM ProductEntity p
			"""
	)
	Page<Product> findAllWithCategories(Pageable pageable);
}
