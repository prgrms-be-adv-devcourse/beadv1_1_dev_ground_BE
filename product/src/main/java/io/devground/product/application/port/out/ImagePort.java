package io.devground.product.application.port.out;

import java.util.List;

import io.devground.product.application.model.vo.ApplicationImageType;

public interface ImagePort {

	List<String> getImageUrls(String productCode, ApplicationImageType imageType);
}
