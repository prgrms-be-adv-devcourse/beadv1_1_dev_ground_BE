package io.devground.dbay.domain.product.product.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.product.category.entity.Category;
import io.devground.dbay.domain.product.category.repository.CategoryRepository;
import io.devground.dbay.domain.product.product.dto.CartProductsRequest;
import io.devground.dbay.domain.product.product.dto.CartProductsResponse;
import io.devground.dbay.domain.product.product.dto.RegistProductRequest;
import io.devground.dbay.domain.product.product.dto.RegistProductResponse;
import io.devground.dbay.domain.product.product.dto.UpdateProductRequest;
import io.devground.dbay.domain.product.product.dto.UpdateProductResponse;
import io.devground.dbay.domain.product.product.entity.Product;
import io.devground.dbay.domain.product.product.entity.ProductSale;
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

		return ProductMapper.registResponseFromProductInfos(product, productSale);
	}

	// TODO: sellerCode 관련 검증 필요
	@Override
	public UpdateProductResponse updateProduct(String sellerCode, String productCode, UpdateProductRequest request) {

		// 같은 유저인지 인가 필요

		Product product = productRepository.findByCode(productCode)
			.orElseThrow(ErrorCode.PRODUCT_NOT_FOUND::throwServiceException);
		ProductSale productSale = product.getProductSale();

		product.changeProductMetadata(request.title(), request.description());
		productSale.changePrice(request.price());

		return ProductMapper.updateResponseFromProductInfo(product, productSale);
	}

	// TODO: sellerCode 관련 검증 필요
	@Override
	public void deleteProduct(String productCode) {

		// 같은 유저인지 인가 필요

		Product product = productRepository.findByCode(productCode)
			.orElseThrow(ErrorCode.PRODUCT_NOT_FOUND::throwServiceException);

		product.delete();
	}

	@Override
	@Transactional(readOnly = true)
	public List<CartProductsResponse> getCartProducts(CartProductsRequest request) {

		List<CartProductsResponse> responses = productRepository.findCartProductsByProductCodes(
			request.productCodes());

		if (responses.isEmpty() || request.productCodes().size() != responses.size()) {
			ErrorCode.PRODUCT_NOT_FOUND.throwServiceException();
		}

		return responses;
	}
}
