package io.devground.dbay.domain.product.product.dto;

import java.net.URL;
import java.util.List;

import lombok.Builder;

@Builder
public record PresignedUrlResponse(
	List<URL> presignedUrls
) {
}
