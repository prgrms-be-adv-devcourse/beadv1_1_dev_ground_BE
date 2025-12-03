package io.devground.product.domain.vo.response;

import java.net.URL;
import java.util.List;

public record RegistProductResponse(

	String productCode,
	String productSaleCode,
	String sellerCode,
	String title,
	String description,
	long price,
	List<URL> presignedUrls
) {
	public static RegistProductResponseBuilder builder() {
		return new RegistProductResponseBuilder();
	}

	public static class RegistProductResponseBuilder {
		private String productCode;
		private String productSaleCode;
		private String sellerCode;
		private String title;
		private String description;
		private long price;
		private List<URL> presignedUrls;

		RegistProductResponseBuilder() {
		}

		public RegistProductResponseBuilder productCode(String productCode) {
			this.productCode = productCode;
			return this;
		}

		public RegistProductResponseBuilder productSaleCode(String productSaleCode) {
			this.productSaleCode = productSaleCode;
			return this;
		}

		public RegistProductResponseBuilder sellerCode(String sellerCode) {
			this.sellerCode = sellerCode;
			return this;
		}

		public RegistProductResponseBuilder title(String title) {
			this.title = title;
			return this;
		}

		public RegistProductResponseBuilder description(String description) {
			this.description = description;
			return this;
		}

		public RegistProductResponseBuilder price(long price) {
			this.price = price;
			return this;
		}

		public RegistProductResponseBuilder presignedUrls(List<URL> presignedUrls) {
			this.presignedUrls = presignedUrls;
			return this;
		}

		public RegistProductResponse build() {
			return new RegistProductResponse(this.productCode, this.productSaleCode, this.sellerCode, this.title,
				this.description, this.price, this.presignedUrls);
		}

		public String toString() {
			return "RegistProductResponse.RegistProductResponseBuilder(productCode=" + this.productCode
				+ ", productSaleCode=" + this.productSaleCode + ", sellerCode=" + this.sellerCode + ", title="
				+ this.title + ", description=" + this.description + ", price=" + this.price + ", presignedUrls="
				+ this.presignedUrls + ")";
		}
	}
}
