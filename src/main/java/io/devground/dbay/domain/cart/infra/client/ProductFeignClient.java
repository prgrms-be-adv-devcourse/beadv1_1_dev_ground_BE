package io.devground.dbay.domain.cart.infra.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.domain.cart.cart.model.vo.CartProductsRequest;
import io.devground.dbay.domain.cart.cart.model.vo.ProductDetailResponse;
import io.devground.dbay.domain.cart.cart.model.vo.CartProductsResponse;

@FeignClient(
	name = "product",
	url = "${external.openfeign-url}",
	path = "/api/products"
)
public interface ProductFeignClient {
	@GetMapping("/{productCode}")
	BaseResponse<ProductDetailResponse> getProductDetail(@PathVariable("productCode") String productCode);

	@PostMapping("/carts")
	BaseResponse<List<CartProductsResponse>> getCartProducts(@RequestBody CartProductsRequest request);
}
