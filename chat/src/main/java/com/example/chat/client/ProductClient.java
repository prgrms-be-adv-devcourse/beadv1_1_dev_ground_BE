package com.example.chat.client;

import com.example.chat.model.dto.response.ProductDetailResponse;
import io.devground.core.model.web.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@FeignClient(
        name = "product",
        url = "http://localhost:8080",
        path = "/api/product"
)
public interface ProductClient {

    @GetMapping("/{productCode}")
    BaseResponse<ProductDetailResponse> getProductDetail(@PathVariable("productCode") String productCode);
}
