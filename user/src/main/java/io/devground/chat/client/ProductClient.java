package io.devground.chat.client;

import io.devground.chat.model.dto.request.CartProductsRequest;
import io.devground.chat.model.dto.response.CartProductsResponse;
import io.devground.chat.model.dto.response.ProductDetailResponse;
import io.devground.core.model.web.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    @PostMapping("/carts")
    BaseResponse<List<CartProductsResponse>> getCartProducts(@RequestBody CartProductsRequest request);
}
