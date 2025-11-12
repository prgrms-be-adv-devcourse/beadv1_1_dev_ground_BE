package io.devground.dbay.domain.product.product.service.impl;

import static io.devground.core.model.vo.ImageType.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.product.category.entity.Category;
import io.devground.dbay.domain.product.category.repository.CategoryRepository;
import io.devground.dbay.domain.product.product.client.ImageClient;
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
	private final ImageClient imageClient;

	// TODO: sellerCode 관련 검증 필요
	@Override
	public RegistProductResponse registProduct(String sellerCode, RegistProductRequest request, MultipartFile[] files) {

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

		// TODO: 이미지 저장 Kafka

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
	public UpdateProductResponse updateProduct(
		String sellerCode, String productCode, MultipartFile[] files, UpdateProductRequest request
	) {

		// 같은 유저인지 인가 필요

		Product product = this.productFindByCode(productCode);
		ProductSale productSale = product.getProductSale();

		product.changeProductMetadata(request.title(), request.description());
		productSale.changePrice(request.price());

		List<String> deleteUrls = request.deleteUrls();

		// 파일 삭제: openFeign - 동기
		if (!CollectionUtils.isEmpty(deleteUrls)) {
			imageClient.deleteAll(
					ProductMapper.toDeleteImagesRequest(
						PRODUCT,
						productCode,
						deleteUrls
					))
				.throwIfNotSuccess();
		}

		// TODO: 파일 등록 Kafka

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
	public Void deleteProduct(String productCode) {

		// 같은 유저인지 인가 필요

		Product product = this.productFindByCode(productCode);

		product.delete();

		// TODO: 이미지 삭제 Kafka - 비동기
/*
		productEventProducer.publishProductImageDelete(
			new ProductImageDeleteEvent(PRODUCT, productCode, List.of())
		);
*/

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

	private Product productFindByCode(String productCode) {

		return productRepository.findByCode(productCode)
			.orElseThrow(ErrorCode.PRODUCT_NOT_FOUND::throwServiceException);
	}
}
