package com.example.chat.client;

import com.example.chat.model.dto.response.ProductDetailResponse;
import io.devground.core.model.web.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(
        name = "product",
        url = "${feign.product-service.url}",
        path = "/api/products"
)
public interface ProductClient {

    @GetMapping("/{productCode}")
    BaseResponse<ProductDetailResponse> getProductDetail(
            @PathVariable("productCode") String productCode,
            @RequestHeader("X-CODE") String userCode
    );
}
