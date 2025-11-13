package io.devground.dbay.domain.order.infra.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.devground.dbay.domain.order.order.model.vo.OrderProductListResponse;

@FeignClient(
	name = "OrderToProduct",
	url = "http://localhost:8000",
	path = "/api/products"
)
public interface ProductFeignClient {
	@PostMapping("/carts")
	List<OrderProductListResponse> productListByCodes(@RequestBody List<String> codes);
}
