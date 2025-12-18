package io.devground.dbay.order.infrastructure.adapter.out.user;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.order.infrastructure.vo.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "user",
        url = "user-service:18080",
        path = "/api/users"
)
public interface UserFeignClient {
    @GetMapping("/")
    BaseResponse<UserResponse> login(@RequestHeader("X-CODE") String userCode);
}
