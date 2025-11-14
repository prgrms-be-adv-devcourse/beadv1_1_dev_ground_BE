package io.devground.dbay.domain.product.product.service.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.devground.core.model.vo.ErrorCode;
import io.devground.core.model.web.PageDto;
import io.devground.core.util.PageUtils;
import io.devground.dbay.domain.product.category.entity.Category;
import io.devground.dbay.domain.product.category.repository.CategoryRepository;
import io.devground.dbay.domain.product.product.dto.CartProductsRequest;
import io.devground.dbay.domain.product.product.dto.CartProductsResponse;
import io.devground.dbay.domain.product.product.dto.GetAllProductsResponse;
import io.devground.dbay.domain.product.product.dto.ProductDetailResponse;
import io.devground.dbay.domain.product.product.dto.ProductImageUrlsRequest;
import io.devground.dbay.domain.product.product.dto.RegistProductRequest;
import io.devground.dbay.domain.product.product.dto.RegistProductResponse;
import io.devground.dbay.domain.product.product.dto.UpdateProductRequest;
import io.devground.dbay.domain.product.product.dto.UpdateProductResponse;
import io.devground.dbay.domain.product.product.entity.Product;
import io.devground.dbay.domain.product.product.entity.ProductSale;
import io.devground.dbay.domain.product.product.infra.kafka.ProductImageSagaOrchestrator;
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
	private final ProductImageSagaOrchestrator productImageSagaOrchestrator;

	@Override
	@Transactional(readOnly = true)
	public PageDto<GetAllProductsResponse> getProducts(Pageable pageable) {

		Pageable convertedPageable = PageUtils.convertToSafePageable(pageable);

		Page<Product> products = productRepository.findAllWithSale(convertedPageable);

		Page<GetAllProductsResponse> responses = products
			.map(product -> ProductMapper.getProductsFromProductInfo(product, product.getProductSale()));

		return PageDto.from(responses);
	}

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

		List<URL> presignedUrls = new ArrayList<>();

		if (!CollectionUtils.isEmpty(request.imageExtensions())) {
			presignedUrls = productImageSagaOrchestrator.startGetPresignedUrlsSaga(product.getCode(),
				request.imageExtensions());
		}

		return ProductMapper.registResponseFromProductInfo(product, productSale, presignedUrls);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductDetailResponse getProductDetail(String productCode) {

		Product product = this.getProductByCode(productCode);

		// TODO: 상품 이미지 가져오기 OpenFeign

		return ProductMapper.detailFromProduct(product);
	}

	// TODO: sellerCode 관련 검증 필요
	@Override
	public UpdateProductResponse updateProduct(
		String sellerCode, String productCode, UpdateProductRequest request
	) {

		Product product = this.getProductByCode(productCode);
		ProductSale productSale = product.getProductSale();

		if (!product.getProductSale().getSellerCode().equals(sellerCode)) {
			ErrorCode.IS_NOT_PRODUCT_OWNER.throwServiceException();
		}

		product.changeProductMetadata(request.title(), request.description());
		productSale.changePrice(request.price());

		List<URL> newPresignedUrls = productImageSagaOrchestrator.startProductImageUpdateSaga(
			productCode, request.deleteUrls(), request.newImageExtensions()
		);

		return ProductMapper.updateResponseFromProductInfo(product, productSale, newPresignedUrls);
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
	public Void deleteProduct(String sellerCode, String productCode) {

		Product product = this.getProductByCode(productCode);

		if (!product.getProductSale().getSellerCode().equals(sellerCode)) {
			ErrorCode.IS_NOT_PRODUCT_OWNER.throwServiceException();
		}

		product.delete();

		productImageSagaOrchestrator.startProductImageAllDeleteSaga(productCode, null);

		return null;
	}

	@Override
	public void updateStatusToSold(CartProductsRequest request) {

		List<Product> products = productRepository.findAllByCodeIn(request.productCodes());

		if (request.productCodes().size() != products.size() || products.isEmpty()) {
			ErrorCode.PRODUCT_NOT_FOUND.throwServiceException();
		}

		products.forEach(p -> p.getProductSale().changeAsSold());
	}

	@Override
	public Void saveImageUrls(String sellerCode, String productCode, ProductImageUrlsRequest request) {

		Product product = getProductByCode(productCode);

		if (!product.getProductSale().getSellerCode().equals(sellerCode)) {
			ErrorCode.IS_NOT_PRODUCT_OWNER.throwServiceException();
		}

		productImageSagaOrchestrator.startProductImageUploadSaga(productCode, request.urls());

		return null;
	}

	@Override
	public Product getProductByCode(String productCode) {

		return productRepository.findByCode(productCode)
			.orElseThrow(ErrorCode.PRODUCT_NOT_FOUND::throwServiceException);
	}
}
