package io.devground.product.infrastructure.adapter.out.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.devground.core.model.vo.ImageType;
import io.devground.core.model.web.BaseResponse;

@FeignClient(name = "image", url = "localhost:8080", path = "/api/images")
public interface ImageClient {

	@GetMapping("/{referenceCode}")
	BaseResponse<List<String>> getImageUrls(@PathVariable String referenceCode, @RequestParam ImageType imageType);
}
