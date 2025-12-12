package io.devground.product.product.application.port.out;

import java.util.List;

public interface ProductViewPort {

	void saveView(String userCode, String productCode);

	List<String> getLatestViewedProductCodes(String userCode, int size);

	void increasePopularCount(String productCode);

	List<String> getTopProductCodes(int size);
}
