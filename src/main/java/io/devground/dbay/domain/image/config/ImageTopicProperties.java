package io.devground.dbay.domain.image.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "images.topic")
public class ImageTopicProperties {

	private String push;
	private String delete;
	private String processed;
	private String processedDlt;
}
