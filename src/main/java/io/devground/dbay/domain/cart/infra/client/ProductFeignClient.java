package io.devground.dbay.domain.cart.infra.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.devground.dbay.domain.cart.cart.model.vo.ProductDetailResponse;
import io.devground.dbay.domain.cart.cart.model.vo.CartProductListResponse;

@FeignClient(
	name = "CartToProduct",
	url = "http://localhost:8080",
	path = "/api/products"
)
public interface ProductFeignClient {
	@GetMapping("/{code}")
	ProductDetailResponse productInfoByCode(@PathVariable("code") String code);

	@PostMapping("/carts")
	List<CartProductListResponse> productListByCodes(@RequestBody List<String> codes);
}
