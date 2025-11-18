package io.devground.dbay.domain.cart.cart.service.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import io.devground.core.commands.cart.CreateCartCommand;
import io.devground.core.commands.cart.DeleteCartCommand;
import io.devground.core.commands.cart.DeleteCartItemsCommand;
import io.devground.core.event.cart.CartCreatedEvent;
import io.devground.core.event.cart.CartCreatedFailedEvent;
import io.devground.core.event.cart.CartDeletedEvent;
import io.devground.core.event.cart.CartDeletedFailedEvent;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.cart.cart.model.entity.Cart;
import io.devground.dbay.domain.cart.cart.model.vo.DeleteItemsByCartRequest;
import io.devground.dbay.domain.cart.cart.repository.CartRepository;
import io.devground.dbay.domain.cart.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@KafkaListener(topics = {
	"${carts.command.topic.join}",
	"${carts.command.topic.order}"
})
@RequiredArgsConstructor
public class CartCommandsHandler {

	private final CartService cartService;

	private final CartRepository cartRepository;

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${carts.event.topic.join}")
	private String cartsJoinUserEventTopicName;

	@Value("${carts.event.topic.order}")
	private String cartsOrderEventTopicName;

	@KafkaHandler
	public void handleCreateCart(@Payload CreateCartCommand command) {
		try {
			Cart savedCart = cartService.createCart(command.userCode());

			CartCreatedEvent event = new CartCreatedEvent(savedCart.getUserCode(), savedCart.getCode());

			kafkaTemplate.send(cartsJoinUserEventTopicName, savedCart.getUserCode(), event);
		} catch (Exception e) {
			log.error("장바구니 생성에서 오류가 발생하였습니다. ", e);

			CartCreatedFailedEvent event = new CartCreatedFailedEvent(command.userCode());

			kafkaTemplate.send(cartsJoinUserEventTopicName, command.userCode(), event);
		}
	}

	@KafkaHandler
	public void handleDeleteCart(@Payload DeleteCartCommand command) {
		try {
			Cart deleteCart = cartService.deleteCart(command.userCode());

			CartDeletedEvent event = new CartDeletedEvent(deleteCart.getUserCode());

			kafkaTemplate.send(cartsJoinUserEventTopicName, command.userCode(), event);
		} catch (Exception e) {
			log.error("장바구니 삭제에서 오류가 발생하였습니다. ", e);

			CartDeletedFailedEvent event = new CartDeletedFailedEvent(command.userCode(), "장바구니 삭제 실패");

			kafkaTemplate.send(cartsJoinUserEventTopicName, command.userCode(), event);
		}
	}

	@KafkaHandler
	public void handleOrderComplete(@Payload DeleteCartItemsCommand command) {
		Cart cart = cartRepository.findByUserCode(command.userCode())
			.orElseThrow(ErrorCode.CART_NOT_FOUND::throwServiceException);

		DeleteItemsByCartRequest request = new DeleteItemsByCartRequest(command.productCodes());

		cartService.deleteItemsByCart(cart.getCode(), request);
	}
}
