package io.devground.dbay.domain.payment.service;

import org.springframework.stereotype.Service;

import io.devground.dbay.domain.payment.model.dto.request.PayRequest;
import io.devground.dbay.domain.payment.model.dto.request.PaymentRequest;
import io.devground.dbay.domain.payment.model.dto.request.TossPayRequest;
import io.devground.dbay.domain.payment.model.entity.Payment;
import org.springframework.transaction.annotation.Transactional;

import io.devground.dbay.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	@Override
	@Transactional
	public Payment pay(String userCode, PayRequest payRequest) {
		Long balance = 1000000L;
		//잔액 확인

		if(payRequest.totalAmount() >= balance) {
			//잔액 충분함 -> 예치금 결제
			payByDeposit(payRequest, balance);
		} else {
			//잔액 불충분함 -> 결제 실패 카프카

		}
		return null;
	}

	@Override
	public Payment refund(String userCode, PayRequest payRequest) {
		return null;
	}

	@Override
	public Payment confirmPayment(PayRequest payRequest) {
		//토스 결제 성공 시 예치금 충전 카프카 커맨드(ChargeDeposit)

		//결제 내역 저장

		return null;
	}

	private Payment payByDeposit(PayRequest payRequest, Long balance) {

		//결제 성공으로 예치금 인출 카프카


		return null;
	}

	@Override
	public Payment payToss(PayRequest payRequest, Long balance) {
		//토스페이로 결제 시도

		return null;
	}

	private boolean processTossPayment(TossPayRequest tossPayRequest) {

		try {
			return true;
		} catch (Exception e) {
			return false;
		}


	}
}