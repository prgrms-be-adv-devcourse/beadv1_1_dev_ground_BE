package io.devground.product.domain.vo.response;

import java.net.URL;
import java.util.List;

public record UpdateProductResponse(

	String productCode,
	String productSaleCode,
	String sellerCode,
	String title,
	String description,

	// TODO: 썸네일

	long price,
	List<URL> presignedUrl
) {
	public static UpdateProductResponseBuilder builder() {
		return new UpdateProductResponseBuilder();
	}

	public static class UpdateProductResponseBuilder {
		private String productCode;
		private String productSaleCode;
		private String sellerCode;
		private String title;
		private String description;
		private long price;
		private List<URL> presignedUrl;

		UpdateProductResponseBuilder() {
		}

		public UpdateProductResponseBuilder productCode(String productCode) {
			this.productCode = productCode;
			return this;
		}

		public UpdateProductResponseBuilder productSaleCode(String productSaleCode) {
			this.productSaleCode = productSaleCode;
			return this;
		}

		public UpdateProductResponseBuilder sellerCode(String sellerCode) {
			this.sellerCode = sellerCode;
			return this;
		}

		public UpdateProductResponseBuilder title(String title) {
			this.title = title;
			return this;
		}

		public UpdateProductResponseBuilder description(String description) {
			this.description = description;
			return this;
		}

		public UpdateProductResponseBuilder price(long price) {
			this.price = price;
			return this;
		}

		public UpdateProductResponseBuilder presignedUrl(List<URL> presignedUrl) {
			this.presignedUrl = presignedUrl;
			return this;
		}

		public UpdateProductResponse build() {
			return new UpdateProductResponse(this.productCode, this.productSaleCode, this.sellerCode, this.title,
				this.description, this.price, this.presignedUrl);
		}

		public String toString() {
			return "UpdateProductResponse.UpdateProductResponseBuilder(productCode=" + this.productCode
				+ ", productSaleCode=" + this.productSaleCode + ", sellerCode=" + this.sellerCode + ", title="
				+ this.title + ", description=" + this.description + ", price=" + this.price + ", presignedUrl="
				+ this.presignedUrl + ")";
		}
	}
}