package io.devground.product.product.application.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.vo.DeleteStatus;
import io.devground.core.model.vo.ImageType;
import io.devground.product.product.application.port.out.ImageClientPort;
import io.devground.product.product.application.port.out.ProductEventPort;
import io.devground.product.product.application.port.out.ProductOrchestrationPort;
import io.devground.product.product.application.port.out.persistence.ProductPersistencePort;
import io.devground.product.product.domain.model.Product;
import io.devground.product.product.domain.model.ProductSale;
import io.devground.product.product.domain.port.in.ProductUseCase;
import io.devground.product.product.domain.vo.ProductSaleSpec;
import io.devground.product.product.domain.vo.ProductSpec;
import io.devground.product.product.domain.vo.ProductStatus;
import io.devground.product.product.domain.vo.pagination.PageDto;
import io.devground.product.product.domain.vo.pagination.PageQuery;
import io.devground.product.product.domain.vo.request.CartProductsDto;
import io.devground.product.product.domain.vo.request.ProductImageUrlsDto;
import io.devground.product.product.domain.vo.request.RegistProductDto;
import io.devground.product.product.domain.vo.request.UpdateProductDto;
import io.devground.product.product.domain.vo.request.UpdateProductSoldDto;
import io.devground.product.product.domain.vo.response.CartProductsResponse;
import io.devground.product.product.domain.vo.response.GetAllProductsResponse;
import io.devground.product.product.domain.vo.response.ProductDetailResponse;
import io.devground.product.product.domain.vo.response.RegistProductResponse;
import io.devground.product.product.domain.vo.response.UpdateProductResponse;
import lombok.RequiredArgsConstructor;

// TODO: Dbay 제거 후 ProductApplication으로 롤백
@Service
@Transactional
@RequiredArgsConstructor
public class ProductApplicationService implements ProductUseCase {

	private final ProductPersistencePort productPort;
	private final ProductOrchestrationPort productOrchestrationPort;
	private final ImageClientPort imagePort;
	private final ProductEventPort productEventPort;

	@Override
	@Transactional(readOnly = true)
	public PageDto<GetAllProductsResponse> getProducts(PageQuery pageRequest) {

		return productPort.getProducts(pageRequest);
	}

	@Override
	public RegistProductResponse registProduct(String sellerCode, RegistProductDto request) {

		// 1. 상품 저장
		Product product = productPort.save(sellerCode, request);

		ProductSale productSale = product.getProductSale();

		String productCode = product.getCode();

		// 2. ES, Vector 인덱싱
		productEventPort.publishCreated(product);

		// 3. PresignedUrl 발급
		List<String> imageExtensions = request.imageExtensions();
		List<URL> presignedUrls = new ArrayList<>();
		if (imageExtensions != null && !imageExtensions.isEmpty()) {
			presignedUrls = imagePort.prepareUploadUrls(ImageType.PRODUCT, productCode, imageExtensions);
		}

		return new RegistProductResponse(product, productSale, presignedUrls);
	}

	@Override
	public Void saveImageUrls(String sellerCode, String productCode, ProductImageUrlsDto request) {

		Product product = productPort.getProductByCode(productCode);
		String productSellerCode = product.getProductSale().getSellerCode();

		productOrchestrationPort.uploadProductImages(sellerCode, productSellerCode, productCode, request.urls());

		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public ProductDetailResponse getProductDetail(String productCode) {

		Product product = productPort.getProductByCode(productCode);

		// 이미지 불러오는 부분
		List<String> imageUrls = imagePort.getImageUrls(productCode, ImageType.PRODUCT);

		return new ProductDetailResponse(product, imageUrls);
	}

	@Override
	public UpdateProductResponse updateProduct(String sellerCode, String productCode, UpdateProductDto request) {

		Product product = productPort.getProductByCode(productCode);
		ProductSale productSale = product.getProductSale();

		ProductSpec updatedProductSpec = new ProductSpec(request.title(), request.description());
		ProductSaleSpec productSaleSpec = new ProductSaleSpec(request.price(), ProductStatus.ON_SALE);

		product.updateSpec(updatedProductSpec);
		productSale.updateSpec(productSaleSpec);

		// 1. 상품 수정
		productPort.updateProduct(sellerCode, product, productSale);

		// 2. ES, Vector 인덱싱
		productEventPort.publishUpdated(product);

		// 3. 이미지 수정 및 필요 시 PresignedUrl 발급
		List<URL> newPresignedUrls = productOrchestrationPort.updateProductImages(
			productCode, request.deleteUrls(), request.newImageExtensions()
		);

		return new UpdateProductResponse(product, productSale, newPresignedUrls);
	}

	@Override
	public Void deleteProduct(String sellerCode, String productCode) {

		Product product = productPort.getProductByCode(productCode);

		// 1. 상품 삭제
		product.updateDeleteStatus(DeleteStatus.Y);
		productPort.deleteProduct(sellerCode, product);

		// 2. ES, Vector 인덱싱
		productEventPort.publishDeleted(product);

		// 3. 이미지 삭제
		productOrchestrationPort.deleteProductImages(productCode);

		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CartProductsResponse> getCartProducts(CartProductsDto request) {

		return productPort.getCartProducts(request);
	}

	// TODO: 다시 한 번 확인해보기 - 단순한 작업을 이렇게 복잡하게 처리할 필요가 있을 것인가에 대하여
	@Override
	public Void updateStatusToSold(String sellerCode, CartProductsDto request) {

		List<Product> products = productPort.getProductsByCodes(sellerCode, request.productCodes());

		for (Product product : products) {
			ProductSale productSale = product.getProductSale();
			Long price = productSale.getProductSaleSpec().price();
			ProductSaleSpec updatedSaleSpec = new ProductSaleSpec(price, ProductStatus.SOLD);

			productSale.updateToSold(updatedSaleSpec);

			UpdateProductSoldDto updatedProductSoldDto = new UpdateProductSoldDto(
				productSale.getProductCode(),
				updatedSaleSpec.productStatus()
			);

			// 1. 상품 판매 완료 처리
			productPort.updateToSold(updatedProductSoldDto);

			// 2. ES, Vector 인덱싱
			productEventPort.publishUpdated(product);
		}

		return null;
	}

	@Override
	public void updateThumbnail(String productCode, String thumbnail) {

		// 1. 상품 썸네일 업데이트
		productPort.updateThumbnail(productCode, thumbnail);

		Product product = productPort.getProductByCode(productCode);

		// 2. ES, Vector 인덱싱
		productEventPort.publishUpdated(product);
	}
}
