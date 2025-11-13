package io.devground.dbay.domain.cart.cart.service.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import io.devground.core.commands.cart.CreateCartCommand;
import io.devground.dbay.domain.cart.cart.model.entity.Cart;
import io.devground.core.event.cart.CartCreatedEvent;
import io.devground.core.event.cart.CartCreatedFailedEvent;
import io.devground.dbay.domain.cart.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@KafkaListener(topics = {
	"${carts.command.topic.name}"
})
@RequiredArgsConstructor
public class CartCommandsHandler {

	private final CartService cartService;

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${carts.event.topic.name}")
	private String cartsEventTopicName;

	@KafkaHandler
	public void handleCreateCart(@Payload CreateCartCommand command) {
		try {
			Cart savedCart = cartService.createCart(command.userCode());

			CartCreatedEvent event = new CartCreatedEvent(savedCart.getUserCode());

			kafkaTemplate.send(cartsEventTopicName, savedCart.getUserCode(), event);
		} catch (Exception e) {
			log.error("장바구니 생성에서 오류가 발생하였습니다. ", e);

			CartCreatedFailedEvent event = new CartCreatedFailedEvent(command.userCode());

			kafkaTemplate.send(cartsEventTopicName, command.userCode() ,event);
		}
	}
}
