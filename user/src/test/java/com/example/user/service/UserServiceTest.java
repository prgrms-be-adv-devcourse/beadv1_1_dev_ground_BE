package com.example.user.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.user.model.dto.request.EmailCertificationRequest;
import com.example.user.model.dto.request.UserRequest;
import com.example.user.model.entity.User;
import com.example.user.repository.UserRepository;

import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private RedisService redisService;

	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	@DisplayName("회원가입 성공 - 이메일 인증이 완료된 경우")
	void registerUser_success() {
		//given
		UserRequest userRequest = new UserRequest(
			"tester",
			"test@test.com",
			"password",
			"test",
			"test",
			"test",
			"test"
		);

		//Redis에서 인증 완료된 상태
		when(redisService.find(eq("test@test.com"), eq(String.class))).thenReturn("Verified");
		//비밀번호 암호화
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedPassword");
		//UserRepository.save() 호출 시 저장된 객체 반환
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		//when
		User savedUser = userService.registerUser(userRequest);

		//then
		assertNotNull(savedUser);
		assertEquals("tester", savedUser.getName());
		assertEquals("test@test.com", savedUser.getEmail());
		assertEquals("encodedPassword", savedUser.getPassword());

		// repository.save가 정확히 한 번 호출됐는지 확인
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	@DisplayName("이메일 인증 성공 시 Redis에 Verified 저장")
	void checkCertificateEmail_success() {
		// given
		EmailCertificationRequest request =
			new EmailCertificationRequest("test@test.com", "1234");

		when(redisService.find("test@test.com", String.class))
			.thenReturn("1234");

		// when
		userService.checkCertificateEmail(request);

		// then
		// 1) 기존 인증코드 삭제
		verify(redisService).delete("test@test.com");

		// 2) Verified 상태 저장 (Duration.ofMinutes(10))
		verify(redisService).save(eq("test@test.com"), eq("Verified"), any(Duration.class));
	}

	@Test
	@DisplayName("이메일 인증 실패 - 코드가 null인 경우 CODE_EXPIRED 예외 발생")
	void checkCertificateEmail_codeNull() {
		// given
		EmailCertificationRequest request =
			new EmailCertificationRequest("test@test.com", null);

		// when & then
		ServiceException e = assertThrows(ServiceException.class, () -> {
			userService.checkCertificateEmail(request);
		});

		assertEquals(ErrorCode.CODE_EXPIRED.getMessage(), e.getMessage());
	}

	@Test
	@DisplayName("이메일 인증 실패 - 잘못된 코드 입력 시 WRONG_VERIFICATION_CODE 예외 발생")
	void checkCertificateEmail_wrongCode() {
		// given
		EmailCertificationRequest request =
			new EmailCertificationRequest("test@test.com", "wrong");

		when(redisService.find("test@test.com", String.class))
			.thenReturn("1234");

		// when & then
		ServiceException e = assertThrows(ServiceException.class, () -> {
			userService.checkCertificateEmail(request);
		});

		assertEquals(ErrorCode.WRONG_VERIFICATION_CODE.getMessage(), e.getMessage());
	}
}
