package io.devground.dbay.order.infrastructure.adapter.out.product;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.order.infrastructure.vo.OrderProductsRequest;
import io.devground.dbay.order.infrastructure.vo.CartProductsResponse;
import io.devground.dbay.order.infrastructure.vo.ProductDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
	name = "product",
	url = "localhost:8000",
	path = "/api/products"
)
public interface ProductFeignClient {
	@GetMapping("/{productCode}")
	BaseResponse<ProductDetailResponse> getProductDetail(@PathVariable("productCode") String productCode);

	@PostMapping("/carts")
	BaseResponse<List<CartProductsResponse>> getCartProducts(@RequestBody OrderProductsRequest request);
}
