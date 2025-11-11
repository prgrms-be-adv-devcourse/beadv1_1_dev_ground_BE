package com.example.user.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.user.model.dto.request.UserRequest;
import com.example.user.model.entity.User;
import com.example.user.repository.UserRepository;

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
}
