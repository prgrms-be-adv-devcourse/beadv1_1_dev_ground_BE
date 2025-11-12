package com.example.user.service;

import java.time.Duration;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.user.mapper.UserMapper;
import com.example.user.model.dto.request.EmailCertificationRequest;
import com.example.user.model.dto.request.UserRequest;
import com.example.user.model.entity.User;
import com.example.user.repository.UserRepository;
import com.example.user.utils.provider.EmailProvider;

import io.devground.core.model.vo.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final EmailProvider emailProvider;
	private final RedisService redisService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserRepository userRepository;

	@Override
	public void sendCertificateEmail(String email) {
		String verificationCode = RandomStringUtils.randomAlphanumeric(10);

		log.info("verificationCode:{}", verificationCode);
		log.info("email:{}", email);

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

	@Override
	public User registerUser(UserRequest userRequest) {
		String email = userRequest.email();

		if (!redisService.find(email, String.class).equals("Verified")
			|| redisService.find(email, String.class) == null) {
			throw ErrorCode.NOT_VERIFICATION_EMAIL.throwServiceException();
		}

		//사용자 정보 저장
		User user = UserMapper.toEntity(userRequest, bCryptPasswordEncoder);
		userRepository.save(user);

		//장바구니 코드 저장

		//예치금 코드 저장

		return user;
	}
}
