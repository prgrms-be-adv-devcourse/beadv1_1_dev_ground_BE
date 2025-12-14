package io.devground.product.product.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.net.URL;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import io.devground.core.model.web.BaseResponse;
import io.devground.product.product.domain.vo.ProductStatus;
import io.devground.product.product.domain.vo.request.ProductImageUrlsDto;
import io.devground.product.product.domain.vo.request.RegistCategoryDto;
import io.devground.product.product.domain.vo.request.RegistProductDto;
import io.devground.product.product.domain.vo.request.UpdateProductDto;
import io.devground.product.product.domain.vo.response.AdminCategoryResponse;
import io.devground.product.product.domain.vo.response.RegistProductResponse;
import io.devground.product.product.domain.vo.response.UpdateProductResponse;
import io.devground.product.product.infrastructure.adapter.out.repository.ProductJpaRepository;
import io.devground.product.product.infrastructure.adapter.out.repository.client.ImageClient;
import io.devground.product.product.infrastructure.model.persistence.ProductEntity;
import io.devground.product.product.infrastructure.saga.repository.SagaRepository;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class ProductImageApplicationTest {

	@Autowired
	ProductApplicationService productService;
	@Autowired
	AdminCategoryApplication categoryService;
	@Autowired
	ProductJpaRepository productRepository;
	@Autowired
	SagaRepository sagaRepository;
	@MockBean
	ImageClient imageClient;

	static final String S3_URL = "https://s3.amazonaw.com/bucket/";

	Long categoryId;
	String sellerCode = "seller";

	@BeforeEach
	void setup() throws Exception {

		AdminCategoryResponse depth1 = categoryService.registCategory(
			new RegistCategoryDto("전자기기", null));
		AdminCategoryResponse depth2 = categoryService.registCategory(
			new RegistCategoryDto("스마트폰", depth1.id()));
		AdminCategoryResponse depth3 = categoryService.registCategory(
			new RegistCategoryDto("아이폰", depth2.id()));

		categoryId = depth3.id();

		given(imageClient.generatePresignedUrls(any()))
			.willReturn(BaseResponse.success(HttpStatus.OK.value(), List.of(
				new URL("https://presigned.url/1.jpg"),
				new URL("https://presigned.url/1.png")
			)));

		given(imageClient.getImageUrls(anyString(), any()))
			.willReturn(BaseResponse.success(HttpStatus.OK.value(), List.of(
				"https://image.url/1.jpg",
				"https://image.url/2.png"
			)));

		given(imageClient.updateImages(any()))
			.willReturn(BaseResponse.success(HttpStatus.OK.value(), List.of(
				new URL("https://new-presigned-url/1.jpg")
			)));
	}

	@Test
	@DisplayName("성공_상품 등록 - 이미지 등록")
	void success_regist_product_with_images() throws Exception {

		// given
		RegistProductDto request = new RegistProductDto(
			categoryId, "아이폰 15 Pro", "최신 아이폰입니다.", 1500000L, List.of("jpg", "png")
		);

		// when
		RegistProductResponse response = productService.registProduct(sellerCode, request);

		// then
		assertThat(response.productCode()).isNotBlank();
		assertThat(response.productSaleCode()).isNotBlank();
		assertThat(response.presignedUrls()).hasSize(2);
		assertThat(response.presignedUrls().getFirst().toString()).contains("presigned.url");

		ProductEntity product = productRepository.findByCode(response.productCode()).get();
		assertEquals("아이폰 15 Pro", product.getTitle());
		assertEquals("최신 아이폰입니다.", product.getDescription());
		assertEquals(1500000L, product.getProductSale().getPrice());
		assertEquals(ProductStatus.ON_SALE, product.getProductSale().getProductStatus());
		assertEquals("전자기기/스마트폰/아이폰", product.getCategory().getFullPath());
	}

	@Test
	@DisplayName("성공_이미지 URL 저장 - Saga 시작 확인")
	void success_save_image_urls() throws Exception {

		// given
		RegistProductDto request = new RegistProductDto(
			categoryId, "아이폰 15", "설명", 1000000L, null
		);

		RegistProductResponse product = productService.registProduct(sellerCode, request);

		ProductImageUrlsDto imageRequest = new ProductImageUrlsDto(
			List.of(
				S3_URL + "image1.jpg", S3_URL + "image2.png"
			)
		);

		// when
		productService.saveImageUrls(sellerCode, product.productCode(), imageRequest);

		// then
		assertThat(sagaRepository.findAll()).isNotEmpty();
		assertEquals(product.productCode(), sagaRepository.findAll().getFirst().getReferenceCode());
	}

	@Test
	@DisplayName("성공_상품 수정 시 이미지 추가")
	void success_update_product_with_new_images() throws Exception {

		// given
		RegistProductDto registRequest = new RegistProductDto(
			categoryId, "아이폰 15", "설명", 1000000L, null
		);

		RegistProductResponse product = productService.registProduct(sellerCode, registRequest);

		UpdateProductDto updateRequest = new UpdateProductDto(
			"아이폰 15 Pro", "새로운 설명", 1500000L, null, List.of("jpg", "png")
		);

		// when
		UpdateProductResponse response = productService.updateProduct(sellerCode, product.productCode(), updateRequest);

		// then
		assertThat(response.presignedUrl()).isNotEmpty();

		verify(imageClient, times(1)).updateImages(any());
	}

	@Test
	@DisplayName("성공_썸네일 업데이트")
	void success_update_thumbnail() throws Exception {

		// given
		RegistProductDto request = new RegistProductDto(
			categoryId, "아이폰 15", "설명", 1000000L, null
		);

		RegistProductResponse product = productService.registProduct(sellerCode, request);

		String thumbnailUrl = "https://thumbnail.url/image.jpg";

		// when
		productService.updateThumbnail(product.productCode(), thumbnailUrl);

		// then
		ProductEntity updatedProduct = productRepository.findByCode(product.productCode()).get();
		assertEquals(thumbnailUrl, updatedProduct.getThumbnailUrl());
	}
}
