package io.devground.dbay.cart.infrastructure.adapter.out.product;

import io.devground.core.model.web.BaseResponse;


import io.devground.dbay.cart.infrastructure.adapter.out.vo.CartProductsRequest;
import io.devground.dbay.cart.infrastructure.adapter.out.vo.CartProductsResponse;
import io.devground.dbay.cart.infrastructure.adapter.out.vo.ProductDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
	name = "product",
	url = "localhost:8086",
	path = "/api/products"
)
public interface ProductFeignClient {
	@GetMapping("/{productCode}")
	BaseResponse<ProductDetailResponse> getProductDetail(@RequestHeader("X-CODE") String userCode, @PathVariable("productCode") String productCode);

	@PostMapping("/carts")
	BaseResponse<List<CartProductsResponse>> getCartProducts(@RequestBody CartProductsRequest request);
}
