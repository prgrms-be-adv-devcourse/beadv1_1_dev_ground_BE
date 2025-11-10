package com.example.user.service;

import java.time.Duration;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import com.example.user.model.dto.request.EmailCertificationRequest;
import com.example.user.utils.provider.EmailProvider;

import io.devground.core.model.vo.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final EmailProvider emailProvider;
	private final RedisService redisService;

	@Override
	public void sendCertificateEmail(String email) {
		String verificationCode = RandomStringUtils.randomAlphanumeric(10);

		emailProvider.sendEmail(email, verificationCode);
	}

	@Override
	public void checkCertificateEmail(EmailCertificationRequest emailCertificationRequest) {
		String code = emailCertificationRequest.code();
		String email = emailCertificationRequest.email();

		String sendCode = redisService.find(email, String.class);

		if (code == null) {
			//코드 만료
			throw ErrorCode.CODE_EXPIRED.throwServiceException();
		}
		if (sendCode.equals(code)) {
			redisService.delete(email);
			redisService.save(email, "Verified", Duration.ofMinutes(10));
		} else {
			throw ErrorCode.WRONG_VERIFICATION_CODE.throwServiceException();
		}
	}
}
