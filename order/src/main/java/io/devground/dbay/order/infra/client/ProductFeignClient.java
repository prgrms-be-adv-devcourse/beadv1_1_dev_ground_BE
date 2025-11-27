package io.devground.dbay.order.infra.client;

import io.devground.core.model.web.BaseResponse;

import io.devground.dbay.order.order.model.vo.CartProductsRequest;
import io.devground.dbay.order.order.model.vo.OrderProductListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
	name = "OrderToProduct",
	url = "${external.openfeign-url}",
	path = "/api/products"
)
public interface ProductFeignClient {
	@PostMapping("/carts")
	BaseResponse<List<OrderProductListResponse>> getCartProducts(@RequestBody CartProductsRequest codes);
}
