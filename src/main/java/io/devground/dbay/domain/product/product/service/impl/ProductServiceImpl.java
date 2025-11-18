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
import io.devground.core.model.vo.ImageType;
import io.devground.core.model.web.PageDto;
import io.devground.core.util.PageUtils;
import io.devground.dbay.domain.product.category.model.entity.Category;
import io.devground.dbay.domain.product.category.repository.CategoryRepository;
import io.devground.dbay.domain.product.product.client.ImageClient;
import io.devground.dbay.domain.product.product.infra.kafka.ProductImageSagaOrchestrator;
import io.devground.dbay.domain.product.product.mapper.ProductMapper;
import io.devground.dbay.domain.product.product.model.dto.CartProductsRequest;
import io.devground.dbay.domain.product.product.model.dto.CartProductsResponse;
import io.devground.dbay.domain.product.product.model.dto.GetAllProductsResponse;
import io.devground.dbay.domain.product.product.model.dto.ProductDetailResponse;
import io.devground.dbay.domain.product.product.model.dto.ProductImageUrlsRequest;
import io.devground.dbay.domain.product.product.model.dto.RegistProductRequest;
import io.devground.dbay.domain.product.product.model.dto.RegistProductResponse;
import io.devground.dbay.domain.product.product.model.dto.UpdateProductRequest;
import io.devground.dbay.domain.product.product.model.dto.UpdateProductResponse;
import io.devground.dbay.domain.product.product.model.entity.Product;
import io.devground.dbay.domain.product.product.model.entity.ProductSale;
import io.devground.dbay.domain.product.product.repository.ProductRepository;
import io.devground.dbay.domain.product.product.repository.ProductSaleRepository;
import io.devground.dbay.domain.product.product.service.ProductIndexService;
import io.devground.dbay.domain.product.product.service.ProductService;
import lombok.AllArgsConstructor;

// TODO: 추후 Index 작업 Kafka 통신으로 변경. 이후 재시도까지 실패 시 알람,
@Service
@Transactional
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ImageClient imageClient;
	private final ProductRepository productRepository;
	private final ProductSaleRepository productSaleRepository;
	private final CategoryRepository categoryRepository;
	private final ProductImageSagaOrchestrator productImageSagaOrchestrator;
	private final ProductIndexService productIndexService;

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

		// TODO: 유저 관련 검증 필요 시 추가

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

		productIndexService.indexProduct(product);

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

		List<String> imageUrls = imageClient.getImages(product.getCode(), ImageType.PRODUCT)
			.throwIfNotSuccess()
			.data();

		return ProductMapper.detailFromProductAndUrls(product, imageUrls);
	}

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

		productIndexService.updateProduct(product);

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

	@Override
	public Void deleteProduct(String sellerCode, String productCode) {

		Product product = this.getProductByCode(productCode);

		if (!product.getProductSale().getSellerCode().equals(sellerCode)) {
			ErrorCode.IS_NOT_PRODUCT_OWNER.throwServiceException();
		}

		product.delete();

		productIndexService.deleteProduct(product);

		productImageSagaOrchestrator.startProductImageAllDeleteSaga(productCode, null);

		return null;
	}

	@Override
	public void updateStatusToSold(String sellerCode, CartProductsRequest request) {

		List<Product> products = productRepository.findAllByCodeIn(request.productCodes());

		// TODO: 인가 더 좋은 방법 생각해보기. 모든 상품 코드 검증이 필요한지 확인
		if (!products.getFirst().getProductSale().getSellerCode().equals(sellerCode)) {
			ErrorCode.IS_NOT_PRODUCT_OWNER.throwServiceException();
		}

		if (request.productCodes().size() != products.size() || products.isEmpty()) {
			ErrorCode.PRODUCT_NOT_FOUND.throwServiceException();
		}

		products.forEach(p -> {
			p.getProductSale().changeAsSold();

			productIndexService.updateProduct(p);
		});
	}

	@Override
	public Void saveImageUrls(String sellerCode, String productCode, ProductImageUrlsRequest request) {

		Product product = getProductByCode(productCode);

		if (!product.getProductSale().getSellerCode().equals(sellerCode)) {
			ErrorCode.IS_NOT_PRODUCT_OWNER.throwServiceException();
		}

		if (CollectionUtils.isEmpty(request.urls())) {
			return null;
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
