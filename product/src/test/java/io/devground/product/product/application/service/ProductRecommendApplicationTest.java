package io.devground.product.product.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.devground.product.product.application.port.out.ProductVectorPort;
import io.devground.product.product.application.port.out.ProductViewPort;
import io.devground.product.product.application.port.out.persistence.ProductPersistencePort;
import io.devground.product.product.domain.model.Category;
import io.devground.product.product.domain.model.Product;
import io.devground.product.product.domain.model.ProductSale;
import io.devground.product.product.domain.vo.ProductRecommendSpec;
import io.devground.product.product.domain.vo.ProductSaleSpec;
import io.devground.product.product.domain.vo.ProductSpec;
import io.devground.product.product.domain.vo.ProductStatus;
import io.devground.product.product.domain.vo.RecommendType;
import io.devground.product.product.domain.vo.response.ProductRecommendResponse;

@ExtendWith(MockitoExtension.class)
class ProductRecommendApplicationTest {

	@InjectMocks
	ProductRecommendApplication productRecommendService;
	@Mock
	ProductPersistencePort productPort;
	@Mock
	ProductViewPort viewPort;
	@Mock
	ProductVectorPort vectorPort;

	static final String USER_CODE = "userCode";
	static final String SELLER_CODE = "seller";
	static final String PRODUCT_CODE = "product";
	static final String REC_CODE = "rec";
	static final String POP_CODE = "pop";
	static final String TEST_THUMBNAIL_URL = "https://placehold.co/600x400/jpg";
	static final int RESULT_CODE_SIZE = 10;

	Product product;
	List<Product> products;
	List<Product> popularProducts;
	List<String> productCodes;
	List<ProductRecommendSpec> recommendSpecs;
	List<ProductRecommendSpec> popularSpecs;

	@BeforeEach
	void setUp() {
		Category category = Category.of(null, "전자기기");
		ProductSpec productSpec = new ProductSpec("아이폰 15 Pro", "최신 아이폰입니다.");
		product = new Product(productSpec, category);

		ProductSaleSpec saleSpec = new ProductSaleSpec(1500000L, ProductStatus.ON_SALE);
		ProductSale productSale = new ProductSale(SELLER_CODE, product.getCode(), saleSpec);
		product.linkProductSale(productSale);
		product.updateThumbnail(TEST_THUMBNAIL_URL);
		product.updateId(1L);

		products = List.of(product);

		popularProducts = this.createPopularProducts(3);

		productCodes = List.of(
			PRODUCT_CODE + "1", PRODUCT_CODE + "2", PRODUCT_CODE + "3", PRODUCT_CODE + "4", PRODUCT_CODE + "5"
		);

		recommendSpecs = List.of(
			new ProductRecommendSpec(
				REC_CODE + "1", "추천상품1", "설명1", 100000L, "전자기기", "url1", ProductStatus.ON_SALE.name()),
			new ProductRecommendSpec(
				REC_CODE + "2", "추천상품2", "설명2", 200000L, "전자기기", "url2", ProductStatus.ON_SALE.name())
		);

		popularSpecs = List.of(
			new ProductRecommendSpec(
				POP_CODE + "1", "인기상품1", "설명1", 10000L, "전자기기", "url3", ProductStatus.ON_SALE.name()),
			new ProductRecommendSpec(
				POP_CODE + "2", "인기상품2", "설명2", 20000L, "전자기기", "url4", ProductStatus.ON_SALE.name())
		);
	}

	private List<Product> createPopularProducts(int count) {
		Category category = Category.of(null, "전자기기");
		List<Product> productList = new java.util.ArrayList<>();

		for (int i = 0; i < count; i++) {
			ProductSpec spec = new ProductSpec("상품" + i, "설명" + i);
			Product p = new Product(spec, category);

			ProductSaleSpec saleSpec = new ProductSaleSpec(100000L * (i + 1), ProductStatus.ON_SALE);
			ProductSale sale = new ProductSale(SELLER_CODE, p.getCode(), saleSpec);
			p.linkProductSale(sale);
			p.updateThumbnail(TEST_THUMBNAIL_URL);
			p.updateId((long) (i + 1));

			productList.add(p);
		}

		return productList;
	}

	@Test
	@DisplayName("성공_사용자 조회 기반 추천 - 조회 5개 이상, 상품 추천 성공")
	void success_rec_by_user_view() throws Exception {

		// given
		String userCode = USER_CODE;
		int size = RESULT_CODE_SIZE;

		given(viewPort.getLatestViewedProductCodes(userCode, size)).willReturn(productCodes);
		given(productPort.getProductsByCodes(productCodes)).willReturn(products);
		given(vectorPort.recommendByUserView(products, size)).willReturn(recommendSpecs);

		// when
		ProductRecommendResponse response = productRecommendService.recommendByUserView(userCode, size);

		// then
		assertEquals(RecommendType.USER_VIEW_HISTORY, response.recommendType());
		assertEquals(REC_CODE + "1", response.recommendSpecs().getFirst().productCode());
		assertThat(response.recommendSpecs()).hasSize(2);

		verify(viewPort, times(1)).getLatestViewedProductCodes(userCode, size);
		verify(productPort, times(1)).getProductsByCodes(productCodes);
		verify(vectorPort, times(1)).recommendByUserView(products, size);
		verify(viewPort, never()).getTopProductCodes(anyInt());
	}

	@Test
	@DisplayName("성공_사용자 조회 기반 추천 - 조회 5개 미만, 폴백 인기순")
	void success_rec_by_user_view_fallback_popular() throws Exception {

		// given
		String userCode = USER_CODE;
		int size = RESULT_CODE_SIZE;
		List<String> fewProductCodes = List.of(PRODUCT_CODE + "1", PRODUCT_CODE + "2", PRODUCT_CODE + "3");

		given(viewPort.getLatestViewedProductCodes(userCode, size)).willReturn(fewProductCodes);
		given(viewPort.getTopProductCodes(size)).willReturn(productCodes);
		given(productPort.getProductsByCodes(productCodes)).willReturn(popularProducts);

		// when
		ProductRecommendResponse response = productRecommendService.recommendByUserView(userCode, size);

		// then
		assertEquals(RecommendType.FALLBACK_POPULAR, response.recommendType());
		assertThat(response.recommendSpecs()).hasSize(3);

		verify(viewPort, times(1)).getLatestViewedProductCodes(userCode, size);
		verify(viewPort, times(1)).getTopProductCodes(size);
		verify(productPort, times(1)).getProductsByCodes(productCodes);
		verify(vectorPort, never()).recommendByUserView(any(), anyInt());
	}

	@Test
	@DisplayName("성공_사용자 조회 기반 추천 - 비회원은 인기순 폴백")
	void success_rec_by_user_view_without_login_fallback_popular() throws Exception {

		// given
		int size = RESULT_CODE_SIZE;

		given(viewPort.getTopProductCodes(size)).willReturn(productCodes);
		given(productPort.getProductsByCodes(productCodes)).willReturn(popularProducts);

		// when
		ProductRecommendResponse response = productRecommendService.recommendByUserView(null, size);

		// then
		assertEquals(RecommendType.FALLBACK_POPULAR, response.recommendType());

		verify(viewPort, never()).getLatestViewedProductCodes(anyString(), anyInt());
		verify(viewPort, times(1)).getTopProductCodes(anyInt());
	}

	@Test
	@DisplayName("성공_사용자 조회 기반 추천 - 벡터 추천 결과 없으면 인기순 폴백")
	void success_rec_by_user_code_without_vector() throws Exception {

		// given
		String userCode = USER_CODE;
		int size = RESULT_CODE_SIZE;

		given(viewPort.getLatestViewedProductCodes(userCode, size)).willReturn(productCodes);
		given(productPort.getProductsByCodes(productCodes))
			.willReturn(products)
			.willReturn(popularProducts);
		given(vectorPort.recommendByUserView(products, size)).willReturn(List.of());
		given(viewPort.getTopProductCodes(size)).willReturn(productCodes);

		// when
		ProductRecommendResponse response = productRecommendService.recommendByUserView(userCode, size);

		// then
		assertEquals(RecommendType.FALLBACK_POPULAR, response.recommendType());

		verify(vectorPort, times(1)).recommendByUserView(products, size);
		verify(viewPort, times(1)).getTopProductCodes(size);
		verify(productPort, times(2)).getProductsByCodes(productCodes);
	}

	@Test
	@DisplayName("성공_상품 상세 기반 추천")
	void success_rec_by_product_detail() throws Exception {

		// given
		String productCode = product.getCode();
		int size = RESULT_CODE_SIZE;

		given(productPort.getProductByCode(productCode)).willReturn(product);
		given(vectorPort.recommendByProductDetail(product, size)).willReturn(recommendSpecs);

		// when
		ProductRecommendResponse response = productRecommendService.recommendByProductDetail(productCode, size);

		// then
		assertEquals(RecommendType.PRODUCT_DETAIL, response.recommendType());
		assertEquals(REC_CODE + "1", response.recommendSpecs().getFirst().productCode());
		assertThat(response.recommendSpecs()).hasSize(2);
	}

	@Test
	@DisplayName("성공_상품 상세 기반 추천 - 결과 없을 때 인기순 폴백")
	void success_rec_by_product_detail_without_result() throws Exception {

		// given
		String productCode = product.getCode();
		int size = RESULT_CODE_SIZE;

		given(productPort.getProductByCode(productCode)).willReturn(product);
		given(vectorPort.recommendByProductDetail(product, size)).willReturn(List.of());
		given(viewPort.getTopProductCodes(size)).willReturn(productCodes);
		given(productPort.getProductsByCodes(productCodes)).willReturn(popularProducts);

		// when
		ProductRecommendResponse response = productRecommendService.recommendByProductDetail(productCode, size);

		// then
		assertEquals(RecommendType.FALLBACK_POPULAR, response.recommendType());

		verify(vectorPort, times(1)).recommendByProductDetail(product, size);
		verify(viewPort, times(1)).getTopProductCodes(size);
	}

	@Test
	@DisplayName("성공_추천 size 파라미터가 null이면 기본값은 10")
	void success_rec_by_null_size() throws Exception {

		// given
		String userCode = USER_CODE;
		int size = RESULT_CODE_SIZE;

		given(viewPort.getLatestViewedProductCodes(userCode, size)).willReturn(productCodes).willReturn(productCodes);
		given(productPort.getProductsByCodes(productCodes)).willReturn(products);
		given(vectorPort.recommendByUserView(products, size)).willReturn(recommendSpecs);

		// when
		productRecommendService.recommendByUserView(userCode, null);

		// then
		verify(vectorPort, times(1)).recommendByUserView(products, size);
	}

	@Test
	@DisplayName("성공_추천 size 파라미터가 30을 초과하면 기본값은 10")
	void success_rec_by_exceeded_size() throws Exception {

		// given
		String userCode = USER_CODE;
		int size = 50;

		given(viewPort.getLatestViewedProductCodes(userCode, RESULT_CODE_SIZE)).willReturn(productCodes)
			.willReturn(productCodes);
		given(productPort.getProductsByCodes(productCodes)).willReturn(products);
		given(vectorPort.recommendByUserView(products, RESULT_CODE_SIZE)).willReturn(recommendSpecs);

		// when
		productRecommendService.recommendByUserView(userCode, size);

		// then
		verify(vectorPort, times(1)).recommendByUserView(products, RESULT_CODE_SIZE);
	}

	@Test
	@DisplayName("성공_추천 size 파라미터가 정상 범위")
	void success_rec_by_in_range_size() throws Exception {

		// given
		String userCode = USER_CODE;
		int size = 20;

		given(viewPort.getLatestViewedProductCodes(userCode, RESULT_CODE_SIZE)).willReturn(productCodes)
			.willReturn(productCodes);
		given(productPort.getProductsByCodes(productCodes)).willReturn(products);
		given(vectorPort.recommendByUserView(products, size)).willReturn(recommendSpecs);

		// when
		productRecommendService.recommendByUserView(userCode, size);

		// then
		verify(vectorPort, times(1)).recommendByUserView(products, size);
	}
}