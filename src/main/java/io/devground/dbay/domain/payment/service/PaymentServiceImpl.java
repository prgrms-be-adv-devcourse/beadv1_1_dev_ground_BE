package io.devground.dbay.domain.payment.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import io.devground.core.event.order.Temp.event.PaymentCreatedEvent;
import io.devground.core.event.order.Temp.event.PaymentCreatedFailed;
import io.devground.core.model.vo.DepositHistoryType;
import io.devground.core.model.vo.ErrorCode;
import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.domain.deposit.dto.response.DepositBalanceResponse;
import io.devground.dbay.domain.payment.infra.DepositFeignClient;
import io.devground.dbay.domain.payment.mapper.PaymentMapper;
import io.devground.dbay.domain.payment.model.dto.request.PaymentRequest;
import io.devground.dbay.domain.payment.model.dto.request.TossPayRequest;
import io.devground.dbay.domain.payment.model.entity.Payment;

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.devground.dbay.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private final DepositFeignClient depositFeignClient;
	private final PaymentRepository paymentRepository;

	@Value("${payments.command.topic.name}")
	private String paymentsCommandTopicName;

	@Value("${deposits.command.topic.name}")
	private String depositsCommandTopicName;

	@Value("${custom.payments.toss.secretKey}")
	private String tossPaySecretKey;

	@Value("${custom.payments.toss.confirm-url}")
	private String tossPayConfirmUrl;

	@Override
	@Transactional
	public Payment pay(String userCode, PaymentRequest paymentRequest) {
		//잔액 확인
		//opneFeign으로 받아오기
		BaseResponse<DepositBalanceResponse> response = depositFeignClient.getBalance(userCode);
		Long balance = response.data().balance();

		if (paymentRequest.totalAmount() >= balance) {
			//잔액 충분함 -> 예치금 결제
			payByDeposit(paymentRequest, balance);
		} else {
			//잔액 불충분함 -> 결제 실패 카프카
			PaymentCreatedFailed paymentCreatedFailed = new PaymentCreatedFailed(paymentRequest.orderCode(),
				paymentRequest.userCode(), "예치금이 부족하여 결제에 실패하였습니다.");
			kafkaTemplate.send(depositsCommandTopicName, paymentCreatedFailed);
		}
		return null;
	}

	@Override
	public Payment refund(String userCode, PaymentRequest paymentRequest) {
		return null;
	}

	@Override
	public Payment confirmPayment(PaymentRequest paymentRequest) {
		//토스 결제 성공 시 예치금 충전 카프카 커맨드(ChargeDeposit)

		//결제 내역 저장
		//payment 저장
		Payment payment = PaymentMapper.toEntity(paymentRequest);
		return paymentRepository.save(payment);
	}

	private Payment payByDeposit(PaymentRequest paymentRequest, Long balance) {
		//결제 성공으로 예치금 인출 카프카
		PaymentCreatedEvent paymentCreatedEvent = new PaymentCreatedEvent(paymentRequest.userCode(),
			paymentRequest.totalAmount(),
			DepositHistoryType.PAYMENT_INTERNAL, paymentRequest.orderCode());

		kafkaTemplate.send(paymentsCommandTopicName, paymentCreatedEvent);
		return null;
	}

	@Override
	public void payToss(PaymentRequest paymentRequest, Long balance) {
		//토스페이로 결제 시도
		TossPayRequest tosspayRequest = new TossPayRequest(paymentRequest.orderCode(),
			paymentRequest.totalAmount() - balance);
		boolean result = processTossPayment(tosspayRequest);

		if (result) {
			//토스페이 결제 성공
			confirmPayment(paymentRequest);
		} else {
			//토스페이 결제 실패
			throw ErrorCode.TOSS_PAY_FAILED.throwServiceException();
		}
	}

	private boolean processTossPayment(TossPayRequest tossPayRequest) {
		try {
			String authorization = "Basic " + Base64.getEncoder().encodeToString((tossPaySecretKey + ":").getBytes(
				StandardCharsets.UTF_8));

			Map<String, Object> requestMap = objectMapper.convertValue(tossPayRequest, new TypeReference<>() {
			});

			HttpClient client = HttpClient.newHttpClient();

			HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(URI.create(tossPayConfirmUrl))
				.header("Authorization", authorization)
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofByteArray(objectMapper.writeValueAsBytes(requestMap)))
				.build();

			HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				return true;
			} else {
				log.error("토스 페이먼츠 결제 중 오류 발생 : {}", response.statusCode());
				log.info("response body = {}", response.body());
				return false;
			}

		} catch (Exception e) {
			log.error("토스 결제 과정 중 오류 발생");
			return false;
		}

	}
}