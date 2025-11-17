package io.devground.dbay.domain.product.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.devground.dbay.domain.product.product.model.dto.CartProductsResponse;
import io.devground.dbay.domain.product.product.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Optional<Product> findByCode(String productCode);

	@Query("""
		SELECT new io.devground.dbay.domain.product.product.model.dto.CartProductsResponse
		(
			p.code,
			ps.code,
			ps.sellerCode,
			p.title,
			ps.price
		)
		FROM Product p
		JOIN p.productSale ps
		WHERE p.code IN :productCodes
		AND ps.productStatus = 'ON_SALE'
		"""
	)
	List<CartProductsResponse> findCartProductsByProductCodes(@Param("productCodes") List<String> productCodes);

	List<Product> findAllByCodeIn(List<String> codes);

	@Query(
		value = """
			SELECT p
			FROM Product p
			JOIN FETCH p.productSale ps
			WHERE p.deleteStatus = 'N'
			""",
		countQuery = """
			SELECT count(p)
			From Product p
			where p.deleteStatus = 'N'
			"""
	)
	Page<Product> findAllWithSale(Pageable pageable);
}
