package io.devground.dbay.domain.deposit.service.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import io.devground.core.commands.CreateDeposit;
import io.devground.core.events.deposit.DepositCreateFailed;
import io.devground.core.events.deposit.DepositCreatedSuccess;
import io.devground.dbay.domain.deposit.dto.response.DepositResponse;
import io.devground.dbay.domain.deposit.service.DepositService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@KafkaListener(topics = {
	"${deposits.command.topic.name}"
})
@RequiredArgsConstructor
public class DepositEventHandler {

	private final DepositService depositService;

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${deposits.event.topic.name}")
	private String depositsEventTopicName;

	@KafkaHandler
	public void handleCommand(@Payload CreateDeposit command) {

		try {

			DepositResponse response = depositService.createDeposit(command.userCode());

			DepositCreatedSuccess depositCreatedEvent = new DepositCreatedSuccess(
				response.userCode(),
				response.depositCode()
			);

			kafkaTemplate.send(depositsEventTopicName, depositCreatedEvent);

		} catch (Exception e) {

			log.error("예치금을 생성하는데 오류가 발생했습니다!", e);

			DepositCreateFailed depositCreateFailed = new DepositCreateFailed(
				command.userCode(),
				"예치금 생성에 실패했어요"
			);

			kafkaTemplate.send(depositsEventTopicName, depositCreateFailed);
		}

	}
}
