package io.devground.dbay.cart.infrastructure.adapter.in.kafka;

import io.devground.core.commands.cart.CreateCartCommand;
import io.devground.core.commands.cart.DeleteCartCommand;
import io.devground.core.commands.cart.DeleteCartItemsCommand;
import io.devground.dbay.cart.application.port.out.kafka.CartEventPort;
import io.devground.dbay.cart.domain.model.Cart;
import io.devground.dbay.cart.domain.port.in.CartUseCase;
import io.devground.dbay.cart.domain.vo.ProductCode;
import io.devground.dbay.cart.domain.vo.UserCode;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = {
        "${carts.command.topic.join}",
        "${carts.command.topic.purchase}"
})
public class CartCommandConsumer {

    private final CartUseCase cartUseCase;
    private final CartEventPort cartEventPort;

    @KafkaHandler
    public void handleCreateCart(@Payload CreateCartCommand command) {
        try {
            Cart cart = cartUseCase.create(new UserCode(command.userCode()));
            cartEventPort.publishCartCreated(cart.getUserCode().value(), cart.getCartCode().value());
        } catch (Exception e) {
            cartEventPort.publishCartCreatedFailed(command.userCode(), "장바구니 생성 실패");
        }
    }

    @KafkaHandler
    public void handleDeleteCart(@Payload DeleteCartCommand command) {
        try {
            cartUseCase.deleteCart(new UserCode(command.userCode()));
            cartEventPort.publishCartDeleted(command.userCode());
        } catch (Exception ex) {
            cartEventPort.publishCartDeletedFailed(command.userCode(), "장바구니 삭제 실패");
        }
    }

    @KafkaHandler
    public void handleOrderComplete(@Payload DeleteCartItemsCommand command) {
            List<ProductCode> productCodes = command.productCodes().stream()
                            .map(ProductCode::new)
                                    .toList();

            cartUseCase.removeCartItems(new UserCode(command.userCode()), productCodes);
            cartEventPort.publishOrderCompleted(command.productCodes());
    }
}
