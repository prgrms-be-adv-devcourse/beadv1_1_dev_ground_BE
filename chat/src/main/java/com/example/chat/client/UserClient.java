package com.example.chat.client;

import com.example.chat.model.dto.response.UserInfoResponse;
import io.devground.core.model.web.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "user",
        url = "${feign.user-service.url}",
        path = "/api/users"
)
public interface UserClient {

    @GetMapping("/")
    BaseResponse<UserInfoResponse> getUser(@RequestHeader("X-CODE") String userCode);
}
