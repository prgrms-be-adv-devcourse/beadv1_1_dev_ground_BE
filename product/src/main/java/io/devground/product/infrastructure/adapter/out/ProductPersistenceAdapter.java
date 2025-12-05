package io.devground.product.infrastructure.adapter.out;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import io.devground.product.application.model.RegistProductDto;
import io.devground.product.application.model.UpdateProductSoldDto;
import io.devground.product.application.port.out.persistence.ProductPersistencePort;
import io.devground.product.domain.model.Product;
import io.devground.product.domain.vo.DomainErrorCode;
import io.devground.product.domain.vo.ProductSaleSpec;
import io.devground.product.domain.vo.pagination.PageDto;
import io.devground.product.domain.vo.pagination.PageQuery;
import io.devground.product.domain.vo.response.GetAllProductsResponse;
import io.devground.product.infrastructure.mapper.PageMapper;
import io.devground.product.infrastructure.mapper.ProductMapper;
import io.devground.product.infrastructure.model.persistence.CategoryEntity;
import io.devground.product.infrastructure.model.persistence.ProductEntity;
import io.devground.product.infrastructure.model.persistence.ProductSaleEntity;
import io.devground.product.infrastructure.util.PageUtils;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductPersistencePort {

	private final CategoryJpaRepository categoryRepository;
	private final ProductJpaRepository productRepository;
	private final ProductSaleJpaRepository productSaleRepository;

	@Override
	public PageDto<GetAllProductsResponse> getProducts(PageQuery pageRequest) {

		Pageable pageable = PageUtils.convertToSafePageable(pageRequest);

		Page<ProductEntity> products = productRepository.findAllWithSale(pageable);

		Page<GetAllProductsResponse> responses = products
			.map(product -> new GetAllProductsResponse(product, product.getProductSale()));

		return PageMapper.from(responses);
	}

	@Override
	public Product getProductByCode(String code) {

		ProductEntity product = getProduct(code);

		return ProductMapper.toProductDomain(product, product.getProductSale());
	}

	@Override
	public Product save(String sellerCode, RegistProductDto request) {

		CategoryEntity categoryEntity = categoryRepository.findById(request.categoryId())
			.orElseThrow(DomainErrorCode.CATEGORY_NOT_FOUND::throwException);

		ProductEntity productEntity = ProductEntity.builder()
			.category(categoryEntity)
			.title(request.title())
			.description(request.description())
			.build();

		productRepository.save(productEntity);

		ProductSaleEntity productSaleEntity = ProductSaleEntity.builder()
			.product(productEntity)
			.price(request.price())
			.sellerCode(sellerCode)
			.build();

		productSaleEntity.addProduct(productEntity);

		productSaleRepository.save(productSaleEntity);

		return ProductMapper.toProductDomain(productEntity, productSaleEntity);
	}

	@Override
	public void updateThumbnail(String productCode, String thumbnail) {

		ProductEntity product = getProduct(productCode);

		product.updateThumbnail(thumbnail);
	}

	@Override
	public List<Product> getProductsByCodes(String sellerCode, List<String> productCodes) {

		List<ProductEntity> products = this.getProducts(productCodes);

		return products.stream()
			.map(p -> ProductMapper.toProductDomain(p, p.getProductSale()))
			.toList();
	}

	@Override
	public void updateToSold(List<UpdateProductSoldDto> updateProductsInfo) {

		updateProductsInfo.forEach(p -> {
				ProductEntity product = getProduct(p.productCode());
				ProductSaleEntity productSale = product.getProductSale();

				ProductSaleSpec updatedSpec = p.productSaleSpec();

				productSale.changeSoldSpec(updatedSpec.price(), updatedSpec.productStatus());
			}
		);
	}

	private ProductEntity getProduct(String code) {
		return productRepository.findByCode(code)
			.orElseThrow(DomainErrorCode.PRODUCT_NOT_FOUND::throwException);
	}

	private List<ProductEntity> getProducts(List<String> productCodes) {

		return productRepository.findProductsByCodes(productCodes);
	}
}
