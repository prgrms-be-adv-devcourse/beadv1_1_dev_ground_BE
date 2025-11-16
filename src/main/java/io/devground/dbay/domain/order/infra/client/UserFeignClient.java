package io.devground.dbay.domain.order.infra.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.devground.dbay.domain.order.order.model.vo.RoleType;

@FeignClient(
	name = "OrderToUser",
	url = "http://localhost:8080",
	path = "/api/users"
)
public interface UserFeignClient {
}
