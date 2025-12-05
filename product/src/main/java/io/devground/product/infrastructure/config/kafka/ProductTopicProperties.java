package io.devground.product.infrastructure.config.kafka;

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

	@Getter
	@Setter
	public static class ImageTopic {
		private String push;
		private String delete;
		private String pushDlt;
		private String deleteDlt;
	}
}
