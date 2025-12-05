package io.devground.product.application.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.vo.ImageType;
import io.devground.product.application.model.CartProductsDto;
import io.devground.product.application.model.ProductImageUrlsDto;
import io.devground.product.application.model.RegistProductDto;
import io.devground.product.application.model.UpdateProductDto;
import io.devground.product.application.model.UpdateProductSoldDto;
import io.devground.product.application.model.vo.ApplicationImageType;
import io.devground.product.application.port.out.ImagePersistencePort;
import io.devground.product.application.port.out.ProductOrchestrationPort;
import io.devground.product.application.port.out.persistence.ProductPersistencePort;
import io.devground.product.application.port.out.persistence.ProductSearchPort;
import io.devground.product.domain.model.Product;
import io.devground.product.domain.model.ProductSale;
import io.devground.product.domain.port.in.ProductUseCase;
import io.devground.product.domain.vo.ProductSaleSpec;
import io.devground.product.domain.vo.ProductSpec;
import io.devground.product.domain.vo.ProductStatus;
import io.devground.product.domain.vo.pagination.PageDto;
import io.devground.product.domain.vo.pagination.PageQuery;
import io.devground.product.domain.vo.response.CartProductsResponse;
import io.devground.product.domain.vo.response.GetAllProductsResponse;
import io.devground.product.domain.vo.response.ProductDetailResponse;
import io.devground.product.domain.vo.response.RegistProductResponse;
import io.devground.product.domain.vo.response.UpdateProductResponse;
import io.devground.product.infrastructure.model.web.request.ImageUploadPlan;
import lombok.RequiredArgsConstructor;

// TODO: Dbay 제거 후 ProductApplication으로 롤백
@Service
@Transactional
@RequiredArgsConstructor
public class ProductApplicationService implements ProductUseCase {

	private final ProductPersistencePort productPort;
	private final ProductSearchPort productSearchPort;
	private final ProductOrchestrationPort productOrchestrationPort;
	private final ImagePersistencePort imagePort;

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
		ProductSpec productSpec = product.getProductSpec();
		ProductSaleSpec productSaleSpec = productSale.getProductSaleSpec();

		String productCode = product.getCode();

		// 2. ES 인덱싱
		productSearchPort.prepareSearch(product);

		// 3. PresignedUrl 발급
		List<String> imageExtensions = request.imageExtensions();
		List<URL> presignedUrls = new ArrayList<>();
		if (imageExtensions != null && !imageExtensions.isEmpty()) {
			presignedUrls = imagePort.prepareUploadUrls(
				new ImageUploadPlan(ImageType.PRODUCT, productCode, imageExtensions)
			);
		}

		return new RegistProductResponse(
			productCode,
			productSale.getCode(),
			sellerCode,
			productSpec.title(),
			productSpec.description(),
			productSaleSpec.price(),
			presignedUrls
		);
	}

	@Override
	public Void saveImageUrls(String sellerCode, String productCode, ProductImageUrlsDto request) {

		Product product = productPort.getProductByCode(productCode);
		String productSellerCode = product.getProductSale().getSellerCode();

		productOrchestrationPort.uploadProductImages(sellerCode, productCode, productSellerCode, request.urls());

		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public ProductDetailResponse getProductDetail(String productCode) {

		Product product = productPort.getProductByCode(productCode);

		// 이미지 불러오는 부분
		List<String> imageUrls = imagePort.getImageUrls(productCode, ApplicationImageType.PRODUCT);

		return new ProductDetailResponse(product, imageUrls);
	}

	@Override
	public UpdateProductResponse updateProduct(String sellerCode, String productCode, UpdateProductDto request) {

		throw new UnsupportedOperationException("구현 중");
	}

	@Override
	public Void deleteProduct(String sellerCode, String productCode) {

		throw new UnsupportedOperationException("구현 중");
	}

	@Override
	@Transactional(readOnly = true)
	public List<CartProductsResponse> getCartProducts(CartProductsDto request) {

		throw new UnsupportedOperationException("구현 중");
	}

	// TODO: 다시 한 번 확인해보기 - 단순한 작업을 이렇게 복잡하게 처리할 필요가 있을 것인가에 대하여
	@Override
	public void updateStatusToSold(String sellerCode, CartProductsDto request) {

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

			// 2. ES 인덱싱
			productSearchPort.updateSearch(product);
		}
	}

	public void updateThumbnail(String productCode, String thumbnail) {

		productPort.updateThumbnail(productCode, thumbnail);
	}
}
