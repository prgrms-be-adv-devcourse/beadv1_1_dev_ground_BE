package io.devground.dbay.domain.product.product.mapper;

import java.net.URL;
import java.util.List;

import io.devground.core.dto.image.DeleteImagesRequest;
import io.devground.core.dto.image.GeneratePresignedRequest;
import io.devground.core.dto.image.UpdateImagesRequest;
import io.devground.core.model.vo.ImageType;
import io.devground.dbay.domain.product.product.dto.GetAllProductsResponse;
import io.devground.dbay.domain.product.product.dto.ProductDetailResponse;
import io.devground.dbay.domain.product.product.dto.RegistProductResponse;
import io.devground.dbay.domain.product.product.dto.UpdateProductResponse;
import io.devground.dbay.domain.product.product.entity.Product;
import io.devground.dbay.domain.product.product.entity.ProductSale;

public abstract class ProductMapper {

	public static GetAllProductsResponse getProductsFromProductInfo(Product product, ProductSale productSale) {

		return GetAllProductsResponse.builder()
			.productCode(product.getCode())
			.title(product.getTitle())
			.price(productSale.getPrice())
			.thumbnailUrl(product.getThumbnailUrl())
			.build();
	}

	public static RegistProductResponse registResponseFromProductInfo(
		Product product, ProductSale productSale, List<URL> presignedUrls
	) {

		return RegistProductResponse.builder()
			.productCode(product.getCode())
			.productSaleCode(productSale.getCode())
			.sellerCode(productSale.getSellerCode())
			.title(product.getTitle())
			.description(product.getDescription())
			.price(productSale.getPrice())
			.presignedUrls(presignedUrls)
			.build();
	}

	public static ProductDetailResponse detailFromProduct(Product product) {

		ProductSale productSale = product.getProductSale();

		return ProductDetailResponse.builder()
			.productCode(product.getCode())
			.productSaleCode(productSale.getCode())
			.sellerCode(productSale.getSellerCode())
			.title(product.getTitle())
			.description(product.getDescription())
			.categoryPath(product.getCategory().getFullPath())
			.price(productSale.getPrice())
			.productStatus(productSale.getProductStatus().getValue())
			.build();
	}

	public static UpdateProductResponse updateResponseFromProductInfo(
		Product product, ProductSale productSale, List<URL> newPresignedUrls
	) {

		return UpdateProductResponse.builder()
			.productCode(product.getCode())
			.productSaleCode(productSale.getCode())
			.sellerCode(productSale.getSellerCode())
			.title(product.getTitle())
			.description(product.getDescription())
			.price(productSale.getPrice())
			.presignedUrl(newPresignedUrls)
			.build();
	}

	public static GeneratePresignedRequest toGeneratePresignedRequest(
		ImageType imageType,
		String referenceCode,
		List<String> fileExtensions
	) {

		return GeneratePresignedRequest.builder()
			.imageType(imageType)
			.referenceCode(referenceCode)
			.fileExtensions(fileExtensions)
			.build();
	}

	public static UpdateImagesRequest toUpdateImagesRequest(
		ImageType imageType,
		String referenceCode,
		List<String> deleteUrls,
		List<String> newImageExtensions
	) {

		return UpdateImagesRequest.builder()
			.imageType(imageType)
			.referenceCode(referenceCode)
			.deleteUrls(deleteUrls)
			.newImageExtensions(newImageExtensions)
			.build();
	}

	public static DeleteImagesRequest toDeleteImagesRequest(
		ImageType imageType,
		String referenceCode
	) {

		return DeleteImagesRequest.builder()
			.imageType(imageType)
			.referenceCode(referenceCode)
			.build();
	}
}
