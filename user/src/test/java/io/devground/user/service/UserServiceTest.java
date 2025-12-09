package io.devground.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.devground.core.events.user.UserCreatedEvent;
import io.devground.core.model.entity.RoleType;
import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.DeleteStatus;
import io.devground.user.model.dto.request.EmailCertificationRequest;
import io.devground.user.model.dto.request.ModifyUserInfoRequest;
import io.devground.user.model.dto.request.UserRequest;
import io.devground.user.model.entity.User;
import io.devground.user.repository.UserRepository;

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

	@Mock
	private KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

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

		//when
		//Redis에서 인증 완료된 상태
		when(redisService.find(eq("test@test.com"), eq(String.class))).thenReturn("Verified");
		//비밀번호 암호화
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedPassword");
		//UserRepository.save() 호출 시 저장된 객체 반환
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
		doReturn(null).when(kafkaTemplate).send(anyString(), anyString(), any());

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

		assertTrue(e.getMessage().contains("인증 코드가 만료되었습니다."));
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

		assertTrue(e.getMessage().contains("인증번호가 올바르지 않습니다."));

	}

	@Nested
	@DisplayName("getByLoginUserCode 호출 시")
	class Describe_getByLoginUserCode {
		final String userCode = "USER_CODE";

		@Nested
		@DisplayName("활성화된 유저가 존재하면")
		class Context_active_user_exists {
			@Test
			void returns_user_response() {
				User user = User.builder()
					.name("tester").email("test@test.com").nickname("nick")
					.phone("010-1234-5678").address("서울").addressDetail("101동")
					.password("pw").role(RoleType.USER)
					.build();

				when(userRepository.findByCodeAndDeleteStatus(userCode, DeleteStatus.N))
					.thenReturn(Optional.of(user));

				io.devground.user.model.dto.response.UserResponse result = userService.getByLoginUserCode(userCode);

				assertAll(
					() -> assertEquals("tester", result.name()),
					() -> assertEquals("test@test.com", result.email()),
					() -> assertEquals("nick", result.nickname()),
					() -> assertEquals("010-1234-5678", result.phone()),
					() -> assertEquals("서울", result.address()),
					() -> assertEquals("101동", result.addressDetail())
				);
				verify(userRepository).findByCodeAndDeleteStatus(userCode, DeleteStatus.N);
			}
		}

		@Nested
		@DisplayName("유저가 없거나 삭제되었다면")
		class Context_user_missing_or_deleted {
			@Test
			void throws_USER_NOT_FOUNT() {
				when(userRepository.findByCodeAndDeleteStatus(userCode, DeleteStatus.N))
					.thenReturn(Optional.empty());

				ServiceException e = assertThrows(ServiceException.class,
					() -> userService.getByLoginUserCode(userCode));

				assertTrue(e.getMessage().contains("사용자를 찾을 수 없습니다."));
				verify(userRepository).findByCodeAndDeleteStatus(userCode, DeleteStatus.N);
			}
		}
	}

	@Nested
	@DisplayName("modifyUserInfo 호출 시")
	class Describe_ModifyUserInfo {
		final String userCode = "USER_CODE";

		final User user = User.builder()
			.name("tester").email("test@test.com").nickname("nick")
			.phone("010-1234-5678").address("서울").addressDetail("101동")
			.password("pw").role(RoleType.USER)
			.build();

		@Nested
		@DisplayName("유저 정보 수정 성공 시")
		class Modify_user_info_success {
			@Test
			@DisplayName("유저 정보 전체 수정")
			void modify_user_all_info_response() {
				ModifyUserInfoRequest request = new ModifyUserInfoRequest("nickname", "010-0000-0000", "경기도 수원시",
					"영통구");

				when(userRepository.findByCode(userCode))
					.thenReturn(Optional.of(user));

				io.devground.user.model.dto.response.ModifyUserInfoResponse result = userService.modifyUserInfo(userCode, request);

				assertEquals("nickname", result.nickname());
				assertEquals("010-0000-0000", result.phone());
				assertEquals("경기도 수원시", result.address());
				assertEquals("영통구", result.addressDetail());
			}

			@Test
			@DisplayName("유저 정보 닉네임 수정")
			void modify_user_nickname_response() {
				ModifyUserInfoRequest request = new ModifyUserInfoRequest("nickname", null, null, null);

				when(userRepository.findByCode(userCode))
					.thenReturn(Optional.of(user));

				io.devground.user.model.dto.response.ModifyUserInfoResponse result = userService.modifyUserInfo(userCode, request);

				assertEquals("nickname", result.nickname());
				assertNull(result.phone());
				assertNull(result.address());
				assertNull(result.addressDetail());

				ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
				verify(userRepository).save(userCaptor.capture());

				User savedUser = userCaptor.getValue();
				assertAll(
					() -> assertEquals("nickname", savedUser.getNickname()),
					() -> assertEquals("010-1234-5678", savedUser.getPhone()),
					() -> assertEquals("서울", savedUser.getAddress()),
					() -> assertEquals("101동", savedUser.getAddressDetail())
				);
			}

			@Test
			@DisplayName("유저 정보 전화번호 수정")
			void modify_user_phone_response() {
				ModifyUserInfoRequest request = new ModifyUserInfoRequest(null, "010-1111-2222", null, null);

				when(userRepository.findByCode(userCode))
					.thenReturn(Optional.of(user));

				io.devground.user.model.dto.response.ModifyUserInfoResponse result = userService.modifyUserInfo(userCode, request);

				assertEquals("010-1111-2222", result.phone());
				assertNull(result.nickname());
				assertNull(result.address());
				assertNull(result.addressDetail());

				ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
				verify(userRepository).save(userCaptor.capture());

				User savedUser = userCaptor.getValue();
				assertAll(
					() -> assertEquals("nick", savedUser.getNickname()),
					() -> assertEquals("010-1111-2222", savedUser.getPhone()),
					() -> assertEquals("서울", savedUser.getAddress()),
					() -> assertEquals("101동", savedUser.getAddressDetail())
				);
			}
		}

		@Nested
		@DisplayName("유저 정보 수정 실패 시")
		class Modify_user_info_fail {

			@Test
			void user_deleted_or_missing() {
				ModifyUserInfoRequest request = new ModifyUserInfoRequest("nickname", "010-12345-2222", null, null);

				when(userRepository.findByCode(userCode))
					.thenReturn(Optional.empty());

				ServiceException e = assertThrows(ServiceException.class,
					() -> userService.modifyUserInfo(userCode, request));

				assertTrue(e.getMessage().contains("사용자를 찾을 수 없습니다."));
				verify(userRepository).findByCode(userCode);
			}
		}

	}
}
