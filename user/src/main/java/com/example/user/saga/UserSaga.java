package com.example.user.saga;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.example.user.service.UserService;

import io.devground.core.commands.cart.CreateCart;
import io.devground.core.commands.cart.DeleteCartCommand;
import io.devground.core.commands.deposit.CreateDeposit;
import io.devground.core.commands.deposit.DeleteDeposit;
import io.devground.core.commands.user.NotifyCartDeleteFailedAlertCommand;
import io.devground.core.commands.user.NotifyCreatedUserAlertCommand;
import io.devground.core.commands.user.NotifyDepositDeleteFailedAlertCommand;
import io.devground.core.commands.user.NotifyUserCreateFailedAlertCommand;
import io.devground.core.event.cart.CartDeletedFailedEvent;
import io.devground.core.events.cart.CartCreateFailed;
import io.devground.core.events.cart.CartCreatedEvent;
import io.devground.core.events.cart.CartDeleteEvent;
import io.devground.core.events.deposit.DepositCreateFailed;
import io.devground.core.events.deposit.DepositCreatedSuccess;
import io.devground.core.events.deposit.DepositDeleteFailed;
import io.devground.core.events.deposit.DepositDeletedSuccess;
import io.devground.core.events.user.UserCreatedEvent;
import io.devground.core.events.user.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@KafkaListener(
	topics = {
		"${users.events.topic.name}"
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

	@Value("${carts.commands.topic.name}")
	private String cartsCommandTopicName;

	//User 생성 성공 -> 예치금
	@KafkaHandler
	public void handleEvent(@Payload UserCreatedEvent event) {
		//예치금 생성
		log.info("user가 생성되어 예치금이 생성됩니다.");
		CreateDeposit createDepositCommand = new CreateDeposit(event.userCode());

		kafkaTemplate.send(depositsCommandTopicName, createDepositCommand);
	}

	//예치금 생성 성공 -> 장바구니
	@KafkaHandler
	public void handleEvent(@Payload DepositCreatedSuccess event) {
		//예치금 코드 저장
		userService.applyDepositCode(event.userCode(), event.depositCode());
		log.info("예치금이 생성되었습니다.");

		//이후 장바구니 생성
		CreateCart createCartCommand = new CreateCart(event.userCode());

		kafkaTemplate.send(cartsCommandTopicName, createCartCommand);
	}

	//장바구니 생성완료 알림
	@KafkaHandler
	public void handleEvent(@Payload CartCreatedEvent event) {
		userService.applyCartCode(event.userCode(), event.carCode());
		log.info("장바구니가 생성되었습니다.");

		NotifyCreatedUserAlertCommand notifyCreatedUserAlertCommand = new NotifyCreatedUserAlertCommand(
			event.userCode());

		kafkaTemplate.send(userCommandTopicName, notifyCreatedUserAlertCommand);
	}

	//장바구니 생성 실패 -> 예치금 삭제
	@KafkaHandler
	public void handleEvent(@Payload CartCreateFailed event) {
		//Command
		DeleteDeposit DeleteDeposit = new DeleteDeposit(event.userCode());
		log.info("장바구니 생성에 실패해 예치금을 삭제하였습니다.");

		kafkaTemplate.send(depositsCommandTopicName, DeleteDeposit);
	}

	//예치금 생성 실패 -> user 삭제
	@KafkaHandler
	public void handleEvent(@Payload DepositCreateFailed event) {
		//Command
		userService.deleteByUserCode(event.userCode());
		log.info("예치금 생성에 실패해 user를 삭제했습니다.");
		NotifyUserCreateFailedAlertCommand notifyUserCreateFailedAlertCommand = new NotifyUserCreateFailedAlertCommand(
			event.userCode());
		kafkaTemplate.send(depositsCommandTopicName, notifyUserCreateFailedAlertCommand);
	}

	//예치금 삭제 -> user 삭제
	@KafkaHandler
	public void handleEvent(@Payload DepositDeletedSuccess event) {

		userService.deleteByUserCode(event.userCode());
		NotifyUserCreateFailedAlertCommand notifyUserCreateFailedAlertCommand = new NotifyUserCreateFailedAlertCommand(
			event.userCode());

		log.info("예치금이 삭제되어 user를 삭제했습니다.");
		kafkaTemplate.send(userCommandTopicName, notifyUserCreateFailedAlertCommand);

	}

	//user 삭제 요청 -> cart 삭제
	@KafkaHandler
	public void handleEvent(@Payload UserDeletedEvent event) {
		DeleteCartCommand deleteCartCommand = new DeleteCartCommand(event.userCode());
		log.info("user 삭제 요청으로 cart를 삭제합니다.");

		kafkaTemplate.send(cartsCommandTopicName, deleteCartCommand);
	}

	//장바구니 삭제 성공 -> 예치금 삭제
	@KafkaHandler
	public void handleEvent(@Payload CartDeleteEvent event) {
		DeleteDeposit deleteDeposit = new DeleteDeposit(event.userCode());
		log.info("user 삭제 요청으로 cart 삭제 후 deposit을 삭제합니다.");

		kafkaTemplate.send(depositsCommandTopicName, deleteDeposit);
	}

	//장바구니 삭제 실패
	@KafkaHandler
	public void handleEvent(@Payload CartDeletedFailedEvent event) {
		NotifyCartDeleteFailedAlertCommand notifyUserCreateFailedAlertCommand = new NotifyCartDeleteFailedAlertCommand(
			event.userCode(), "장바구니 삭제에 실패했습니다.");
		log.info("장바구니 삭제에 실패했습니다.");

		kafkaTemplate.send(userCommandTopicName, notifyUserCreateFailedAlertCommand);
	}

	//예치금 삭제 실패
	@KafkaHandler
	public void handleEvent(@Payload DepositDeleteFailed event) {
		NotifyDepositDeleteFailedAlertCommand notifyUserCreateFailedAlertCommand = new NotifyDepositDeleteFailedAlertCommand(
			event.userCode(), "예치금 삭제에 실패했습니다.");
		log.info("예치금 삭제에 실패했습니다.");

		kafkaTemplate.send(userCommandTopicName, notifyUserCreateFailedAlertCommand);
	}
}
