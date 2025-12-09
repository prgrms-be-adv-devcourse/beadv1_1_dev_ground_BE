package io.devground.user.saga;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import io.devground.core.commands.cart.CreateCartCommand;
import io.devground.core.commands.cart.DeleteCartCommand;
import io.devground.core.commands.deposit.CreateDeposit;
import io.devground.core.commands.deposit.DeleteDeposit;
import io.devground.core.commands.user.NotifyCartDeleteFailedAlertCommand;
import io.devground.core.commands.user.NotifyCreatedUserAlertCommand;
import io.devground.core.commands.user.NotifyDepositDeleteFailedAlertCommand;
import io.devground.core.commands.user.NotifyUserCreateFailedAlertCommand;
import io.devground.core.event.cart.CartCreatedEvent;
import io.devground.core.event.cart.CartCreatedFailedEvent;
import io.devground.core.event.cart.CartDeletedEvent;
import io.devground.core.event.cart.CartDeletedFailedEvent;
import io.devground.core.event.deposit.DepositCreateFailed;
import io.devground.core.event.deposit.DepositCreatedSuccess;
import io.devground.core.event.deposit.DepositDeleteFailed;
import io.devground.core.event.deposit.DepositDeletedSuccess;
import io.devground.core.events.user.UserCreatedEvent;
import io.devground.core.events.user.UserDeletedEvent;
import io.devground.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@KafkaListener(
	topics = {
		"${users.events.topic.join}",
		"${deposits.events.topic.join}",
		"${carts.events.topic.join}"
	}
)
@RequiredArgsConstructor
public class UserSaga {

	private final UserService userService;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${users.commands.topic.join}")
	private String userJoinCommandTopicName;

	@Value("${deposits.commands.topic.join}")
	private String depositsJoinUserCommandTopicName;

	@Value("${carts.commands.topic.join}")
	private String cartsJoinUserCommandTopicName;

	//User 생성 성공 -> 예치금
	@KafkaHandler
	public void handleEvent(@Payload UserCreatedEvent event) {
		//예치금 생성
		log.info("user가 생성되어 예치금이 생성됩니다.");
		CreateDeposit createDepositCommand = new CreateDeposit(event.userCode());

		kafkaTemplate.send(depositsJoinUserCommandTopicName, event.userCode(), createDepositCommand);
	}

	//예치금 생성 성공 -> 장바구니
	@KafkaHandler
	public void handleEvent(@Payload DepositCreatedSuccess event) {
		//예치금 코드 저장
		userService.applyDepositCode(event.userCode(), event.depositCode());
		log.info("예치금이 생성되었습니다.");

		//이후 장바구니 생성
		CreateCartCommand createCartCommand = new CreateCartCommand(event.userCode());

		kafkaTemplate.send(cartsJoinUserCommandTopicName, event.userCode(), createCartCommand);
	}

	//장바구니 생성완료 알림
	@KafkaHandler
	public void handleEvent(@Payload CartCreatedEvent event) {
		userService.applyCartCode(event.userCode(), event.cartCode());
		log.info("장바구니가 생성되었습니다.");

		NotifyCreatedUserAlertCommand notifyCreatedUserAlertCommand = new NotifyCreatedUserAlertCommand(
			event.userCode());

		// kafkaTemplate.send(userJoinCommandTopicName, notifyCreatedUserAlertCommand);
	}

	//장바구니 생성 실패 -> 예치금 삭제
	@KafkaHandler
	public void handleEvent(@Payload CartCreatedFailedEvent event) {
		//Command
		DeleteDeposit DeleteDeposit = new DeleteDeposit(event.userCode());
		log.info("장바구니 생성에 실패해 예치금을 삭제하였습니다.");

		kafkaTemplate.send(depositsJoinUserCommandTopicName, event.userCode(), DeleteDeposit);
	}

	//예치금 생성 실패 -> user 삭제
	@KafkaHandler
	public void handleEvent(@Payload DepositCreateFailed event) {
		//Command
		userService.deleteByUserCode(event.userCode());
		log.info("예치금 생성에 실패해 user를 삭제했습니다.");
		NotifyUserCreateFailedAlertCommand notifyUserCreateFailedAlertCommand = new NotifyUserCreateFailedAlertCommand(
			event.userCode());
		// kafkaTemplate.send(depositsJoinUserCommandTopicName, notifyUserCreateFailedAlertCommand);
	}

	//예치금 삭제 -> user 삭제
	@KafkaHandler
	public void handleEvent(@Payload DepositDeletedSuccess event) {

		userService.deleteByUserCode(event.userCode());
		NotifyUserCreateFailedAlertCommand notifyUserCreateFailedAlertCommand = new NotifyUserCreateFailedAlertCommand(
			event.userCode());

		log.info("예치금이 삭제되어 user를 삭제했습니다.");
		kafkaTemplate.send(userJoinCommandTopicName, event.userCode(), notifyUserCreateFailedAlertCommand);

	}

	//user 삭제 요청 -> cart 삭제
	@KafkaHandler
	public void handleEvent(@Payload UserDeletedEvent event) {
		DeleteCartCommand deleteCartCommand = new DeleteCartCommand(event.userCode());
		log.info("user 삭제 요청으로 cart를 삭제합니다.");

		kafkaTemplate.send(cartsJoinUserCommandTopicName, event.userCode(), deleteCartCommand);
	}

	//장바구니 삭제 성공 -> 예치금 삭제
	@KafkaHandler
	public void handleEvent(@Payload CartDeletedEvent event) {
		DeleteDeposit deleteDeposit = new DeleteDeposit(event.userCode());
		log.info("user 삭제 요청으로 cart 삭제 후 deposit을 삭제합니다.");

		kafkaTemplate.send(depositsJoinUserCommandTopicName, event.userCode(), deleteDeposit);
	}

	//장바구니 삭제 실패
	@KafkaHandler
	public void handleEvent(@Payload CartDeletedFailedEvent event) {
		NotifyCartDeleteFailedAlertCommand notifyUserCreateFailedAlertCommand = new NotifyCartDeleteFailedAlertCommand(
			event.userCode(), "장바구니 삭제에 실패했습니다.");
		log.info("장바구니 삭제에 실패했습니다.");

		kafkaTemplate.send(userJoinCommandTopicName, event.userCode(), notifyUserCreateFailedAlertCommand);
	}

	//예치금 삭제 실패
	@KafkaHandler
	public void handleEvent(@Payload DepositDeleteFailed event) {
		NotifyDepositDeleteFailedAlertCommand notifyUserCreateFailedAlertCommand = new NotifyDepositDeleteFailedAlertCommand(
			event.userCode(), "예치금 삭제에 실패했습니다.");
		log.info("예치금 삭제에 실패했습니다.");

		kafkaTemplate.send(userJoinCommandTopicName, event.userCode(), notifyUserCreateFailedAlertCommand);
	}
}
