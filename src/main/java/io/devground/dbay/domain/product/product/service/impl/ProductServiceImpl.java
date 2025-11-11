package io.devground.dbay.domain.product.product.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.devground.core.event.image.ImagePushEvent;
import io.devground.core.model.vo.ErrorCode;
import io.devground.core.model.vo.ImageType;
import io.devground.dbay.domain.product.category.entity.Category;
import io.devground.dbay.domain.product.category.repository.CategoryRepository;
import io.devground.dbay.domain.product.product.dto.CartProductsRequest;
import io.devground.dbay.domain.product.product.dto.CartProductsResponse;
import io.devground.dbay.domain.product.product.dto.ProductDetailResponse;
import io.devground.dbay.domain.product.product.dto.RegistProductRequest;
import io.devground.dbay.domain.product.product.dto.RegistProductResponse;
import io.devground.dbay.domain.product.product.dto.UpdateProductRequest;
import io.devground.dbay.domain.product.product.dto.UpdateProductResponse;
import io.devground.dbay.domain.product.product.entity.Product;
import io.devground.dbay.domain.product.product.entity.ProductSale;
import io.devground.dbay.domain.product.product.infra.kafka.ProductEventProducer;
import io.devground.dbay.domain.product.product.mapper.ProductMapper;
import io.devground.dbay.domain.product.product.repository.ProductRepository;
import io.devground.dbay.domain.product.product.repository.ProductSaleRepository;
import io.devground.dbay.domain.product.product.service.ProductService;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final ProductSaleRepository productSaleRepository;
	private final CategoryRepository categoryRepository;
	private final ProductEventProducer productEventProducer;

	// TODO: sellerCode 관련 검증 필요
	@Override
	public RegistProductResponse registProduct(String sellerCode, RegistProductRequest request) {

		// 유저 관련 검증 필요 시 추가

		Category category = categoryRepository.findById(request.categoryId())
			.orElseThrow(ErrorCode.CATEGORY_NOT_FOUND::throwServiceException);

		Product product = Product.builder()
			.category(category)
			.title(request.title())
			.description(request.description())
			.build();

		productRepository.save(product);

		ProductSale productSale = ProductSale.builder()
			.product(product)
			.price(request.price())
			.sellerCode(sellerCode)
			.build();

		productSale.addProduct(product);
		productSaleRepository.save(productSale);

		// kafka를 통한 S3 이미지 등록
		if (CollectionUtils.isEmpty(request.fileExtension())) {
			productEventProducer.publishProductImagePush(
				new ImagePushEvent(ImageType.PRODUCT, product.getCode(), request.fileExtension()));
		}

		return ProductMapper.registResponseFromProductInfo(product, productSale);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductDetailResponse getProductDetail(String productCode) {

		Product product = this.productFindByCode(productCode);

		return ProductMapper.detailFromProduct(product);
	}

	// TODO: sellerCode 관련 검증 필요
	@Override
	public UpdateProductResponse updateProduct(String sellerCode, String productCode, UpdateProductRequest request) {

		// 같은 유저인지 인가 필요

		Product product = this.productFindByCode(productCode);
		ProductSale productSale = product.getProductSale();

		product.changeProductMetadata(request.title(), request.description());
		productSale.changePrice(request.price());

		return ProductMapper.updateResponseFromProductInfo(product, productSale);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CartProductsResponse> getCartProducts(CartProductsRequest request) {

		List<CartProductsResponse> responses = productRepository.findCartProductsByProductCodes(request.productCodes());

		if (responses.isEmpty() || request.productCodes().size() != responses.size()) {
			ErrorCode.PRODUCT_NOT_FOUND.throwServiceException();
		}

		return responses;
	}

	// TODO: sellerCode 관련 검증 필요
	@Override
	public void deleteProduct(String productCode) {

		// 같은 유저인지 인가 필요

		Product product = this.productFindByCode(productCode);

		product.delete();
	}

	@Override
	public void updateStatusToSold(CartProductsRequest request) {

		List<Product> products = productRepository.findAllByCodeIn(request.productCodes());

		if (request.productCodes().size() != products.size() || products.isEmpty()) {
			ErrorCode.PRODUCT_NOT_FOUND.throwServiceException();
		}

		products.forEach(p -> p.getProductSale().changeAsSold());
	}

	private Product productFindByCode(String productCode) {

		return productRepository.findByCode(productCode)
			.orElseThrow(ErrorCode.PRODUCT_NOT_FOUND::throwServiceException);
	}
}
