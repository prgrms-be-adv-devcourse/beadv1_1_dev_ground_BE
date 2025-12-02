package com.example.user.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.user.mapper.UserMapper;
import com.example.user.model.dto.request.ChangePasswordRequest;
import com.example.user.model.dto.request.EmailCertificationRequest;
import com.example.user.model.dto.request.ModifyUserInfoRequest;
import com.example.user.model.dto.request.UserRequest;
import com.example.user.model.dto.response.ChangePasswordResponse;
import com.example.user.model.dto.response.ModifyUserInfoResponse;
import com.example.user.model.dto.response.UserResponse;
import com.example.user.model.entity.User;
import com.example.user.repository.UserRepository;
import com.example.user.utils.provider.EmailProvider;

import io.devground.core.events.user.UserCreatedEvent;
import io.devground.core.events.user.UserDeletedEvent;
import io.devground.core.model.vo.DeleteStatus;
import io.devground.core.model.vo.ErrorCode;
import jakarta.mail.MessagingException;
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
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${users.events.topic.join}")
	private String userJoinEventsTopicName;

	@Override
	public void sendCertificateEmail(String email) throws MessagingException {
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

		if (findUserByEmail(email)) {
			throw ErrorCode.USER_ALREADY_EXIST.throwServiceException();
		}

		//사용자 정보 저장
		User user = UserMapper.toEntity(userRequest, bCryptPasswordEncoder);
		User savedUser = userRepository.save(user);

		//유저 생성 이벤트 발행
		String userCode = savedUser.getCode();
		UserCreatedEvent event = new UserCreatedEvent(userCode);
		log.info("Sending event: {}", event);
		kafkaTemplate.send(userJoinEventsTopicName, event.userCode(), event);

		//웰컴 쿠폰 발급

		return user;
	}

	@Override
	public User getByUserCode(String userCode) {
		return userRepository.findByCode(userCode)
			.orElseThrow(() -> new IllegalArgumentException("해당 회원은 존재하지 않습니다."));
	}

	@Override
	public UserResponse getByLoginUserCode(String userCode) {
		User user = userRepository.findByCodeAndDeleteStatus(userCode, DeleteStatus.N)
			.orElseThrow(ErrorCode.USER_NOT_FOUNT::throwServiceException);
		return new UserResponse(user.getName(), user.getEmail(), user.getNickname(), user.getPhone(),
			user.getAddress(), user.getAddressDetail());
	}

	@Override
	@Transactional
	public void applyDepositCode(String userCode, String depositCode) {
		User user = getByUserCode(userCode);
		user.setDepositCode(depositCode);
	}

	@Override
	@Transactional
	public void applyCartCode(String userCode, String cartCode) {
		User user = getByUserCode(userCode);
		user.setCartCode(cartCode);
	}

	@Override
	public void deleteByUserCode(String userCode) {
		User findUser = getByUserCode(userCode);
		findUser.delete();
	}

	@Override
	public void requestDeleteUser(String userCode) {
		UserDeletedEvent event = new UserDeletedEvent(userCode);
		kafkaTemplate.send(userJoinEventsTopicName, event.userCode(), event);
	}

	@Override
	public ChangePasswordResponse changePassword(String userCode, ChangePasswordRequest request) {
		User user = userRepository.findByCode(userCode).orElseThrow(ErrorCode.USER_NOT_FOUNT::throwServiceException);
		if (!request.newPassword().equals(request.newPasswordCheck())) {
			throw ErrorCode.PASSWORD_CONFIRM_MISMATCH.throwServiceException();
		}

		String password = bCryptPasswordEncoder.encode(request.password());

		if (user.getPassword().equals(password)) {
			user.setPassword(bCryptPasswordEncoder.encode(request.newPassword()));
		}

		return new ChangePasswordResponse(userCode, LocalDateTime.now());
	}

	@Override
	public ModifyUserInfoResponse modifyUserInfo(String userCode, ModifyUserInfoRequest request) {
		User user = userRepository.findByCode(userCode).orElseThrow(ErrorCode.USER_NOT_FOUNT::throwServiceException);

		user.modifyUser(request.nickname(), request.phone(), request.address(), request.addressDetail());
		userRepository.save(user);

		return new ModifyUserInfoResponse(request.nickname(), request.phone(), request.address(),
			request.addressDetail());
	}

	private boolean findUserByEmail(String email) {
		return userRepository.existsByEmailAndOauthIdIsNull(email);
	}
}
