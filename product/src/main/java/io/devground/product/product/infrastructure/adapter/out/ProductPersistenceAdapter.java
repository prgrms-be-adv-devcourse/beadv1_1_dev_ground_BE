package io.devground.product.product.infrastructure.adapter.out;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import io.devground.core.model.vo.ErrorCode;
import io.devground.product.common.util.PageUtils;
import io.devground.product.product.application.port.out.persistence.ProductPersistencePort;
import io.devground.product.product.domain.model.Product;
import io.devground.product.product.domain.model.ProductSale;
import io.devground.product.product.domain.vo.ProductDomainErrorCode;
import io.devground.product.product.domain.vo.ProductSpec;
import io.devground.product.product.domain.vo.ProductStatus;
import io.devground.product.product.domain.vo.pagination.PageDto;
import io.devground.product.product.domain.vo.pagination.PageQuery;
import io.devground.product.product.domain.vo.request.CartProductsDto;
import io.devground.product.product.domain.vo.request.RegistProductDto;
import io.devground.product.product.domain.vo.request.UpdateProductSoldDto;
import io.devground.product.product.domain.vo.response.CartProductsResponse;
import io.devground.product.product.domain.vo.response.GetAllProductsResponse;
import io.devground.product.product.infrastructure.adapter.out.repository.CategoryJpaRepository;
import io.devground.product.product.infrastructure.adapter.out.repository.ProductJpaRepository;
import io.devground.product.product.infrastructure.adapter.out.repository.ProductSaleJpaRepository;
import io.devground.product.product.infrastructure.mapper.PageMapper;
import io.devground.product.product.infrastructure.mapper.ProductMapper;
import io.devground.product.product.infrastructure.model.persistence.CategoryEntity;
import io.devground.product.product.infrastructure.model.persistence.ProductEntity;
import io.devground.product.product.infrastructure.model.persistence.ProductSaleEntity;
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
			.orElseThrow(ProductDomainErrorCode.CATEGORY_NOT_FOUND::throwException);

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
	public void updateToSold(UpdateProductSoldDto updateProductsSoldDto) {

		ProductEntity product = getProduct(updateProductsSoldDto.productCode());

		product.getProductSale().updateProductStatus(updateProductsSoldDto.productStatus());
	}

	@Override
	public void updateProduct(String sellerCode, Product product, ProductSale productSale) {

		ProductEntity productEntity = getProduct(product.getCode());
		ProductSaleEntity productSaleEntity = productEntity.getProductSale();

		if (!productSaleEntity.getSellerCode().equals(sellerCode)) {
			ErrorCode.IS_NOT_PRODUCT_OWNER.throwServiceException();
		}

		ProductSpec productSpec = product.getProductSpec();

		productEntity.changeProductMetadata(productSpec.title(), productSpec.description());
		productSaleEntity.changePrice(productSale.getProductSaleSpec().price());
	}

	@Override
	public void deleteProduct(String sellerCode, Product product) {

		ProductEntity productEntity = getProduct(product.getCode());

		if (!productEntity.getProductSale().getSellerCode().equals(sellerCode)) {
			ErrorCode.IS_NOT_PRODUCT_OWNER.throwServiceException();
		}

		productEntity.delete();
	}

	@Override
	public List<CartProductsResponse> getCartProducts(CartProductsDto request) {

		List<CartProductsResponse> responses = productRepository.findCartProductsByProductCodes(
			request.productCodes(), ProductStatus.ON_SALE);

		if (responses.isEmpty() || request.productCodes().size() != responses.size()) {
			ErrorCode.PRODUCT_NOT_FOUND.throwServiceException();
		}

		return responses;
	}

	private ProductEntity getProduct(String code) {
		return productRepository.findByCode(code)
			.orElseThrow(ProductDomainErrorCode.PRODUCT_NOT_FOUND::throwException);
	}

	private List<ProductEntity> getProducts(List<String> productCodes) {

		return productRepository.findProductsByCodes(productCodes);
	}
}
