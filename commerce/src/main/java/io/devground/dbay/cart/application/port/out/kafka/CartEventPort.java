package io.devground.dbay.cart.application.port.out.kafka;

import java.util.List;

public interface CartEventPort {
    void publishCartCreated(String userCode, String cartCode);
    void publishCartCreatedFailed(String userCode, String msg);
    void publishCartDeleted(String userCode);
    void publishCartDeletedFailed(String userCode, String msg);
    void publishOrderCompleted(List<String> productCodes);
}
