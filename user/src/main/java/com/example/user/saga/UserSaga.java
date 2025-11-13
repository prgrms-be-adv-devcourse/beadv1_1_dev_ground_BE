package com.example.user.saga;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.example.user.service.UserService;

import io.devground.core.commands.cart.CreateCart;
import io.devground.core.commands.deposit.CreateDeposit;
import io.devground.core.commands.user.NotifyCreatedUserAlertCommand;
import io.devground.core.commands.user.NotifyUserCreateFailedAlertCommand;
import io.devground.core.events.cart.CartCreateFailed;
import io.devground.core.events.cart.CartCreatedEvent;
import io.devground.core.events.deposit.DepositCreateFailed;
import io.devground.core.events.deposit.DepositCreatedSuccess;
import io.devground.core.events.user.UserCreatedEvent;
import lombok.RequiredArgsConstructor;

@Component
@KafkaListener(
	topics = {
		"${accounts.events.topic.name}"
	}
)
@RequiredArgsConstructor
public class UserSaga {

	private final UserService userService;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${users.commands.topic.name}")
	private String userCommandTopicName;

	@Value("${deposits.commands.topic.name}")
	private String depositsCommandTopicName;

	@Value("${carts.command.topic.name}")
	private String cartsCommandTopicName;

	//예치금
	@KafkaHandler
	public void handleEvent(@Payload UserCreatedEvent event) {
		//예치금 생성
		CreateDeposit createDepositCommand = new CreateDeposit(event.userCode());

		kafkaTemplate.send(depositsCommandTopicName, createDepositCommand);
	}

	//장바구니
	@KafkaHandler
	public void handleEvent(@Payload DepositCreatedSuccess event) {
		//예치금 코드 저장
		userService.applyDepositCode(event.userCode(), event.depositCode());

		//이후 장바구니 생성
		CreateCart createCartCommand = new CreateCart(event.userCode());

		kafkaTemplate.send(cartsCommandTopicName, createCartCommand);
	}

	//생성완료 알림
	@KafkaHandler
	public void handleEvent(@Payload CartCreatedEvent event) {
		userService.applyCartCode(event.userCode(), event.carCode());

		NotifyCreatedUserAlertCommand notifyCreatedUserAlertCommand = new NotifyCreatedUserAlertCommand(
			event.userCode());

		kafkaTemplate.send(userCommandTopicName, notifyCreatedUserAlertCommand);
	}

	//장바구니 생성 실패
	@KafkaHandler
	public void handleEvent(@Payload CartCreateFailed event) {
		DepositCreateFailed depositCreateFailed = new DepositCreateFailed(event.userCode(), "예치금 생성에 실패했습니다.");

		kafkaTemplate.send(depositsCommandTopicName, depositCreateFailed);
	}

	//예치금 생성 실패
	@KafkaHandler
	public void handleEvent(@Payload DepositCreateFailed event) {

		NotifyUserCreateFailedAlertCommand notifyUserCreateFailedAlertCommand = new NotifyUserCreateFailedAlertCommand(
			event.userCode());

		kafkaTemplate.send(depositsCommandTopicName, notifyUserCreateFailedAlertCommand);

	}


}
