package io.devground.product.product.infrastructure.config.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "products.topic")
public class ProductTopicProperties {

	private ImageTopic image;
	private PurchaseTopic purchase;

	@Getter
	@Setter
	public static class ImageTopic {
		private String push;
		private String delete;
		private String pushDlt;
		private String deleteDlt;
	}

	@Getter
	@Setter
	public static class PurchaseTopic {
		private String sold;
		private String soldDlt;
	}
}
