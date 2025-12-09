package io.devground.dbay.ddddeposit.infrastructure.adapter.in.web.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.devground.core.util.feign.FeignConfig;

@TestConfiguration
@EnableFeignClients(basePackageClasses = DepositFeignClient.class)
@Import(FeignConfig.class)
public class TestFeignClientConfig {

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}