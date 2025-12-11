package io.devground.payments.web.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import io.devground.core.util.feign.FeignConfig;

@TestConfiguration
@EnableFeignClients(basePackageClasses = DepositFeignClient.class)
@Import(FeignConfig.class)
public class TestFeignClientConfig {

}