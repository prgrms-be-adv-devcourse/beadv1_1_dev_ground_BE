package io.devground.dbay.domain.order.infra.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.domain.order.order.model.vo.CartProductsRequest;
import io.devground.dbay.domain.order.order.model.vo.OrderProductListResponse;

@FeignClient(
	name = "OrderToProduct",
	url = "${external.openfeign-url}",
	path = "/api/products"
)
public interface ProductFeignClient {
	@PostMapping("/carts")
	BaseResponse<List<OrderProductListResponse>> getCartProducts(@RequestBody CartProductsRequest codes);
}
