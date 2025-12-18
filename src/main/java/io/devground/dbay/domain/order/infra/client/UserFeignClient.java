package io.devground.dbay.domain.order.infra.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
	name = "OrderToUser",
	url = "${external.openfeign-url}",
	path = "/api/users"
)
public interface UserFeignClient {
}
