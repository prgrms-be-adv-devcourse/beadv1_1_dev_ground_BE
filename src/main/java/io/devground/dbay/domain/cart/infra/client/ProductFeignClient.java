package io.devground.dbay.domain.cart.infra.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.devground.dbay.domain.cart.cart.model.vo.ProductDetailResponse;

@FeignClient(
	name = "product",
	url = "http://localhost:8080",
	path = "/api/products"
)
public interface ProductFeignClient {
	@GetMapping("/{code}")
	ProductDetailResponse productInfoByCode(@PathVariable("code") String code);
}
