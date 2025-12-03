package io.devground.product.domain.vo.response;

import io.devground.product.infrastructure.model.persistence.ProductEntity;
import io.devground.product.infrastructure.model.persistence.ProductSaleEntity;

public record GetAllProductsResponse(

	String productCode,
	String title,
	String thumbnailUrl,
	long price
) {
	public GetAllProductsResponse(ProductEntity product, ProductSaleEntity productSale) {
		this(
			product.getCode(),
			product.getTitle(),
			product.getThumbnailUrl(),
			productSale.getPrice()
		);
	}

	public static GetAllProductsResponseBuilder builder() {
		return new GetAllProductsResponseBuilder();
	}

	public static class GetAllProductsResponseBuilder {
		private String productCode;
		private String title;
		private String thumbnailUrl;
		private long price;

		GetAllProductsResponseBuilder() {
		}

		public GetAllProductsResponseBuilder productCode(String productCode) {
			this.productCode = productCode;
			return this;
		}

		public GetAllProductsResponseBuilder title(String title) {
			this.title = title;
			return this;
		}

		public GetAllProductsResponseBuilder thumbnailUrl(String thumbnailUrl) {
			this.thumbnailUrl = thumbnailUrl;
			return this;
		}

		public GetAllProductsResponseBuilder price(long price) {
			this.price = price;
			return this;
		}

		public GetAllProductsResponse build() {
			return new GetAllProductsResponse(this.productCode, this.title, this.thumbnailUrl, this.price);
		}

		public String toString() {
			return "GetAllProductsResponse.GetAllProductsResponseBuilder(productCode=" + this.productCode + ", title="
				+ this.title + ", thumbnailUrl=" + this.thumbnailUrl + ", price=" + this.price + ")";
		}
	}
}
