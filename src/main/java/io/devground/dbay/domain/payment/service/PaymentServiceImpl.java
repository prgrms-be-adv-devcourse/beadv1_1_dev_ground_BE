package io.devground.dbay.domain.payment.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.devground.core.commands.payment.PaymentChargeDepositCommand;
import io.devground.core.model.vo.DepositHistoryType;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.payment.infra.DepositFeignClient;
import io.devground.dbay.domain.payment.mapper.PaymentMapper;
import io.devground.dbay.domain.payment.model.dto.request.PaymentRequest;
import io.devground.dbay.domain.payment.model.dto.request.TossPayRequest;
import io.devground.dbay.domain.payment.model.dto.response.TossPayResponse;
import io.devground.dbay.domain.payment.model.entity.Payment;
import io.devground.dbay.domain.payment.model.vo.PaymentStatus;
import io.devground.dbay.domain.payment.model.vo.PaymentType;
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

	@Value("${payments.event.topic.name}")
	private String paymentsEventTopicName;

	@Value("${custom.payments.toss.secretKey}")
	private String tossPaySecretKey;

	@Value("${custom.payments.toss.confirm-url}")
	private String tossPayConfirmUrl;

	@Override
	@Transactional
	public boolean pay(String userCode, String orderCode, Long totalAmount, String paymentKey) {
		//잔액 확인
		//opneFeign으로 받아오기
		Long balance = depositFeignClient.getBalance(userCode).throwIfNotSuccess().data().balance();

		PaymentRequest paymentRequest = new PaymentRequest(userCode, orderCode, paymentKey, totalAmount,
			PaymentStatus.PENDING);
		Payment payment = PaymentMapper.toEntity(paymentRequest);
		paymentRepository.save(payment);

		if (balance < totalAmount) {
			payment.setPaymentStatus(PaymentStatus.FAILED);
			paymentRepository.save(payment);  // 상태 업데이트 반영
			return false;
		}

		return balance >= totalAmount;
	}

	@Override
	public String getOrderCode(String userCode, Long totalAmount) {
		String orderCode = UUID.randomUUID().toString();

		PaymentRequest paymentRequest = new PaymentRequest(userCode, orderCode, "", totalAmount,
			PaymentStatus.PENDING);
		Payment payment = PaymentMapper.toEntity(paymentRequest);
		Payment response = paymentRepository.save(payment);

		return response.getOrderCode();
	}


	@Override
	public Payment refund(String orderCode, Long amount) {
		Payment payment = getByOrderCode(orderCode);

		return null;
	}

	@Override
	@Transactional
	public void applyDepositPayment(String orderCode) {
		Payment payment = getByOrderCode(orderCode);
		payment.setPaymentStatus(PaymentStatus.COMPLETED);
	}

	@Override
	public void canceledDepositPayment(String orderCode) {
		Payment payment = getByOrderCode(orderCode);
		payment.setPaymentStatus(PaymentStatus.CANCELLED);

	}

	@Override
	public String confirmTossPayment(TossPayRequest tossPayRequest) {
		PaymentChargeDepositCommand paymentChargeDepositCommand = new PaymentChargeDepositCommand(
			tossPayRequest.userCode(), tossPayRequest.chargeAmount(), tossPayRequest.orderCode(),
			DepositHistoryType.CHARGE_TOSS);
		//토스 결제 성공 시 예치금 충전 카프카 커맨드(ChargeDeposit)
		kafkaTemplate.send(paymentsEventTopicName, paymentChargeDepositCommand);

		Payment payment = getByOrderCode(tossPayRequest.orderCode());
		payment.setPaymentStatus(PaymentStatus.COMPLETED);
		payment.setPaymentType(PaymentType.TOSS);
		return payment.getCode();
	}

	@Override
	public TossPayResponse payToss(PaymentRequest paymentRequest, Long balance) {
		//토스페이로 결제 시도
		TossPayRequest tosspayRequest = new TossPayRequest(paymentRequest.userCode(), paymentRequest.orderCode(),
			paymentRequest.paymentKey(), paymentRequest.totalAmount() - balance);
		boolean result = processTossPayment(tosspayRequest);

		if (result) {
			//토스페이 결제 성공
			String paymentCode = confirmTossPayment(tosspayRequest);
			TossPayResponse response = new TossPayResponse(paymentCode, paymentRequest.paymentKey());

			return response;
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

	@Override
	public Payment getByOrderCode(String orderCode) {
		return paymentRepository.findByOrderCode(orderCode);
	}
}