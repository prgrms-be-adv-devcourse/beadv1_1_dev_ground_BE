package io.devground.dbay.deposit.service.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import io.devground.core.commands.deposit.ChargeDeposit;
import io.devground.core.commands.deposit.CreateDeposit;
import io.devground.core.commands.deposit.DeleteDeposit;
import io.devground.core.commands.deposit.RefundDeposit;
import io.devground.core.commands.deposit.SettlementChargeDeposit;
import io.devground.core.commands.deposit.WithdrawDeposit;
import io.devground.core.dto.deposit.response.DepositHistoryResponse;
import io.devground.core.dto.deposit.response.DepositResponse;
import io.devground.core.event.deposit.DepositChargeFailed;
import io.devground.core.event.deposit.DepositChargedSuccess;
import io.devground.core.event.deposit.DepositCreateFailed;
import io.devground.core.event.deposit.DepositCreatedSuccess;
import io.devground.core.event.deposit.DepositDeleteFailed;
import io.devground.core.event.deposit.DepositDeletedSuccess;
import io.devground.core.event.deposit.DepositRefundFailed;
import io.devground.core.event.deposit.DepositRefundedSuccess;
import io.devground.core.event.deposit.DepositWithdrawFailed;
import io.devground.core.event.deposit.DepositWithdrawnSuccess;
import io.devground.core.event.deposit.SettlementDepositChargedSuccess;

import io.devground.dbay.deposit.entity.vo.DepositHistoryType;
import io.devground.dbay.deposit.service.DepositService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@KafkaListener(topics = {
	"${deposits.command.topic.name}",
	"${deposits.command.topic.join}",
	"${deposits.command.topic.purchase}"
})
public class DepositEventHandler {

	private final DepositService depositService;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final String depositsEventTopicName;
	private final String depositsJoinEventTopicName;
	private final String depositsPurchaseEventTopicName;

	public DepositEventHandler(DepositService depositService, KafkaTemplate<String, Object> kafkaTemplate,
		@Value("${deposits.event.topic.name}") String depositsEventTopicName,
		@Value("${deposits.event.topic.join}") String depositsJoinEventTopicName,
		@Value("${deposits.event.topic.purchase}") String depositsPurchaseEventTopicName
		) {
		this.depositService = depositService;
		this.kafkaTemplate = kafkaTemplate;
		this.depositsEventTopicName = depositsEventTopicName;
		this.depositsJoinEventTopicName = depositsJoinEventTopicName;
		this.depositsPurchaseEventTopicName = depositsPurchaseEventTopicName;
	}

	/**
	 * 예치금 생성
	 */
	@KafkaHandler
	public void handleCommand(@Payload CreateDeposit command) {

		try {

			DepositResponse response = depositService.createDeposit(command.userCode());

			DepositCreatedSuccess depositCreatedEvent = new DepositCreatedSuccess(
				response.userCode(),
				response.depositCode()
			);

			kafkaTemplate.send(depositsJoinEventTopicName, command.userCode(), depositCreatedEvent);

		} catch (Exception e) {

			log.error("예치금을 생성하는데 오류가 발생했습니다!", e);

			DepositCreateFailed depositCreateFailed = new DepositCreateFailed(
				command.userCode(),
				"예치금 생성에 실패했어요"
			);

			kafkaTemplate.send(depositsJoinEventTopicName, command.userCode(), depositCreateFailed);
		}

	}

	/**
	 * 예치금 충전
	 */
	@KafkaHandler
	public void handleCommand(@Payload ChargeDeposit command) {

		try {

			DepositHistoryType type = DepositHistoryType.valueOf(command.type().name());
			DepositHistoryResponse response = depositService.charge(
				command.userCode(),
				type,
				command.amount()
			);

			DepositChargedSuccess depositChargedSuccessEvent = new DepositChargedSuccess(
				response.userCode(),
				response.code(),
				response.amount(),
				response.balanceAfter()
			);

			kafkaTemplate.send(depositsEventTopicName, depositChargedSuccessEvent);

			log.info("예치금 충전 완료: userCode={}, amount={}", command.userCode(), command.amount());

		} catch (Exception e) {

			log.error("예치금을 충전하는데 오류가 발생했습니다!", e);

			DepositChargeFailed depositChargeFailed = new DepositChargeFailed(
				command.userCode(),
				command.amount(),
				"예치금 충전에 실패했어요"
			);

			kafkaTemplate.send(depositsEventTopicName, depositChargeFailed);
		}

	}

	/**
	 * 예치금 인출
	 */
	@KafkaHandler
	public void handleCommand(@Payload WithdrawDeposit command) {

		try {

			DepositHistoryType type = DepositHistoryType.valueOf(command.type().name());
			DepositHistoryResponse response = depositService.withdraw(
				command.userCode(),
				type,
				command.amount()
			);

			DepositWithdrawnSuccess depositWithdrawnSuccessEvent = new DepositWithdrawnSuccess(
				response.userCode(),
				response.code(),
				response.amount(),
				response.balanceAfter(),
				command.orderCode(),
				command.productCodes()
			);

			kafkaTemplate.send(depositsPurchaseEventTopicName, command.orderCode(), depositWithdrawnSuccessEvent);

			log.info("예치금 인출 완료: userCode={}, amount={}", command.userCode(), command.amount());

		} catch (Exception e) {

			log.error("예치금을 인출하는데 오류가 발생했습니다!", e);

			DepositWithdrawFailed depositWithdrawFailed = new DepositWithdrawFailed(
				command.userCode(),
				command.amount(),
				"예치금 인출에 실패했어요",
				command.orderCode()
			);

			kafkaTemplate.send(depositsPurchaseEventTopicName, command.orderCode(), depositWithdrawFailed);
		}

	}

	/**
	 * 예치금 환불
	 */
	@KafkaHandler
	public void handleCommand(@Payload RefundDeposit command) {

		try {

			DepositHistoryType type = DepositHistoryType.valueOf(command.type().name());
			DepositHistoryResponse response = depositService.refund(
				command.userCode(),
				type,
				command.amount()
			);

			DepositRefundedSuccess depositRefundedSuccessEvent = new DepositRefundedSuccess(
				response.userCode(),
				response.code(),
				response.amount(),
				response.balanceAfter()
			);

			kafkaTemplate.send(depositsEventTopicName, depositRefundedSuccessEvent);

			log.info("예치금 환불 완료: userCode={}, amount={}", command.userCode(), command.amount());

		} catch (Exception e) {

			log.error("예치금을 환불하는데 오류가 발생했습니다!", e);

			DepositRefundFailed depositRefundFailed = new DepositRefundFailed(
				command.userCode(),
				command.amount(),
				"예치금 환불에 실패했어요"
			);

			kafkaTemplate.send(depositsEventTopicName, depositRefundFailed);
		}

	}

	/**
	 * 예치금 삭제
	 */
	@KafkaHandler
	public void handleCommand(@Payload DeleteDeposit command) {

		try {

			depositService.deleteDeposit(command.userCode());

			kafkaTemplate.send(depositsJoinEventTopicName, command.userCode(), new DepositDeletedSuccess(command.userCode(), "예치금 삭제 완료"));

			log.info("예치금 삭제 완료: userCode={}", command.userCode());

		} catch (Exception e) {

			log.error("예치금을 삭제 하는데 오류가 발생했습니다!", e);

			DepositDeleteFailed depositDeleteFailed = new DepositDeleteFailed(command.userCode(),
				"예치금 삭제에 실패했어요"
			);

			kafkaTemplate.send(depositsJoinEventTopicName, command.userCode(), depositDeleteFailed);
		}

	}

	/**
	 * 정산 예치금 충전
	 * Settlement에서 판매자에게 정산금을 입금
	 */
	@KafkaHandler
	public void handleCommand(@Payload SettlementChargeDeposit command) {

		try {

			DepositHistoryResponse response = depositService.charge(
				command.userCode(),
				DepositHistoryType.SETTLEMENT,
				command.amount()
			);

			SettlementDepositChargedSuccess event = new SettlementDepositChargedSuccess(
				response.userCode(),
				response.code(),
				response.amount(),
				response.balanceAfter(),
				command.orderCode()
			);

			kafkaTemplate.send(depositsEventTopicName, event);

			log.info("정산 예치금 충전 완료: userCode={}, amount={}, orderCode={}",
				command.userCode(), command.amount(), command.orderCode());

		} catch (Exception e) {

			log.error("정산 예치금을 충전하는데 오류가 발생했습니다!", e);

			// TODO: SettlementDepositChargeFailed 이벤트 생성 필요
			DepositChargeFailed depositChargeFailed = new DepositChargeFailed(
				command.userCode(),
				command.amount(),
				"정산 예치금 충전에 실패했어요"
			);

			kafkaTemplate.send(depositsEventTopicName, depositChargeFailed);
		}

	}
}
