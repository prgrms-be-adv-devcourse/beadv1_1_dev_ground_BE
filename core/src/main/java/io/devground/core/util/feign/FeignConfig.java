package io.devground.core.util.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.codec.ErrorDecoder;

@Configuration
public class FeignConfig {

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
		return new CustomFeignErrorDecoder(objectMapper);
	}
}
