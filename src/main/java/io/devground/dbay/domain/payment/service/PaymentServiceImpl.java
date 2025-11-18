package io.devground.dbay.domain.payment.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.devground.dbay.domain.payment.infra.DepositFeignClient;
import io.devground.dbay.domain.payment.model.dto.request.PaymentRequest;
import io.devground.dbay.domain.payment.model.entity.Payment;
import io.devground.dbay.domain.payment.model.vo.PaymentConfirmRequest;
import io.devground.dbay.domain.payment.model.vo.PaymentStatus;
import io.devground.dbay.domain.payment.model.vo.PaymentType;
import io.devground.dbay.domain.payment.model.vo.TossPaymentsRequest;
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
	public Payment process(String userCode, PaymentConfirmRequest request) {
		Long balance = depositFeignClient.getBalance(userCode).data().balance();

		// 예치금 우선 사용
		if (balance > request.amount()) {

			Payment depositPayment = pay(getPaymentRequest(userCode, PaymentType.DEPOSIT, request));

			if (request.amount() > 0) {
				return pay(getPaymentRequest(userCode, PaymentType.TOSS_PAYMENT, request));
			}

			return depositPayment;

		}

		return pay(getPaymentRequest(userCode, PaymentType.TOSS_PAYMENT, request));

	}

	private PaymentRequest getPaymentRequest(String userCode, PaymentType type, PaymentConfirmRequest request) {
		return new PaymentRequest(userCode, type, request.paymentKey(), request.orderCode(), request.amount());
	}


	private Payment handleDepositPayment(String userCode, String orderCode, Long amount) {

		Payment payment = Payment.builder()
			.userCode(userCode)
			.orderCode(orderCode)
			.amount(amount)
			.build();

		payment.setPaymentStatus(PaymentStatus.PAYMENT_COMPLETED);

		return paymentRepository.save(payment);

	}

	@Override
	@Transactional
	public Payment pay(PaymentRequest request) {

		Payment payment;

		if (request.getPaymentType() == PaymentType.DEPOSIT) {
			payment = handleDepositPayment(request.getUserCode(), request.getOrderCode(), request.getAmount());
		} else if (request.getPaymentType() == PaymentType.TOSS_PAYMENT) {
			payment = handleTossPayment(request.getUserCode(), request.getOrderCode(), request.getPaymentKey(), request.getAmount());
		} else {
			throw new UnsupportedOperationException("결제 형식이 잘못되었습니다.");
		}

		return payment;

	}


	private Payment handleTossPayment(String userCode, String orderCode, String paymentKey, Long amount) {

		// 토스페이먼츠 결제 시도
		boolean result = processTossPayment(new TossPaymentsRequest(paymentKey, orderCode, amount.toString()));

		if (!result)
			throw new IllegalStateException("토스페이먼츠 결제에 실패하였습니다.");

		// 결제 성공시 예치금 처리
		//카프카 전송

		// 결제 내역 저장
		Payment payment = Payment.builder()
			.userCode(userCode)
			.orderCode(orderCode)
			.amount(amount)
			.build();

		payment.setPaymentStatus(PaymentStatus.PAYMENT_COMPLETED);


		return paymentRepository.save(payment);

	}

	private boolean processTossPayment(TossPaymentsRequest request) {

		try {
			// 1. Authorization Header 생성
			String authorization = "Basic " + Base64.getEncoder()
				.encodeToString((tossPaySecretKey + ":").getBytes(StandardCharsets.UTF_8));

			// 2. 요청 데이터 구성
			Map<String, Object> requestMap = objectMapper.convertValue(request, new TypeReference<>() {
			});

			// 3. HTTP 요청 구성
			HttpClient client = HttpClient.newHttpClient();

			HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(URI.create(tossPayConfirmUrl))
				.header("Authorization", authorization)
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofByteArray(objectMapper.writeValueAsBytes(requestMap)))
				.build();

			// 4. HTTP 요청 수행
			HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			// 5. 응답 처리
			if (response.statusCode() == HttpStatus.OK.value()) {
				return true;
			} else {
				log.error("토스페이먼츠 결제 수행 과정에서 오류가 발생하였습니다. 다시 시도하여 주시기 바랍니다. 응답코드 : {}", response.statusCode());
				log.info("response.body() = {}", response.body());
				return false;
			}
		} catch (Exception e) {
			log.error("토스페이먼츠 결제 수행 과정에서 오류가 발생하였습니다.");
			return false;
		}


	}

	@Override
	@Transactional
	public Payment refund(String userCode, PaymentRequest request) {

		Payment payment = paymentRepository.findByOrderCode(request.getOrderCode())
			.orElseThrow(() -> new IllegalArgumentException("결제 내역을 찾을 수 없습니다."));

		return payment;
	}

	@Override
	@Transactional
	public Payment confirmPayment(PaymentRequest request) {
		throw new UnsupportedOperationException("");
	}
}