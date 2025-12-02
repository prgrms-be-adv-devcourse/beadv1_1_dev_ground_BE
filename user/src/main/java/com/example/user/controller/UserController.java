package com.example.user.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.model.dto.request.ChangePasswordRequest;
import com.example.user.model.dto.request.EmailCertificationRequest;
import com.example.user.model.dto.request.KakaoCodeRequest;
import com.example.user.model.dto.request.ModifyUserInfoRequest;
import com.example.user.model.dto.request.UserRequest;
import com.example.user.model.dto.response.ChangePasswordResponse;
import com.example.user.model.dto.response.LoginResponse;
import com.example.user.model.dto.response.ModifyUserInfoResponse;
import com.example.user.model.dto.response.UserResponse;
import com.example.user.service.KakaoService;
import com.example.user.service.UserService;

import io.devground.core.model.web.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "UserController")
public class UserController {
	private final UserService userService;
	private final KakaoService kakaoService;

	@Operation(summary = "인증메일 전송 API", description = "인증 메일을 전송합니다.")
	@PostMapping("/send")
	public BaseResponse<String> sendCertificateEmail(@RequestBody UserRequest userRequest) throws MessagingException {
		userService.sendCertificateEmail(userRequest.email());

		return BaseResponse.success(200, userRequest.email(), "인증메일 전송 성공");
	}

	@Operation(summary = "메일 인증 API", description = "메일로 보낸 인증 코드를 확인합니다")
	@PostMapping("/check")
	public BaseResponse<String> checkCertificateEmail(
		@RequestBody @Valid EmailCertificationRequest emailCertificationRequest) {
		userService.checkCertificateEmail(emailCertificationRequest);

		return BaseResponse.success(200, emailCertificationRequest.email(), "이메일 인증 성공");
	}

	@Operation(summary = "회원가입 API", description = "회원가입 API입니다.")
	@PostMapping("/register")
	public BaseResponse<String> registerUser(@RequestBody @Valid UserRequest userRequest) {
		userService.registerUser(userRequest);

		return BaseResponse.success(200, userRequest.email(), "회원가입 성공");
	}

	@Operation(summary = "회원탈퇴 API", description = "회원탈퇴 API입니다.")
	@DeleteMapping("/")
	public BaseResponse<String> deleteUser(@RequestHeader("X-CODE") String userCode) {
		userService.requestDeleteUser(userCode);

		return BaseResponse.success(200, "회원탈퇴 성공");
	}

	@Operation(summary = "카카오 로그인 API", description = "카카오 로그인 API 입니다.")
	@GetMapping("/kakaoLogin")
	public BaseResponse<LoginResponse> kakaoLogin(@RequestParam("code") String code, HttpServletResponse response,
		HttpServletRequest request) throws IOException {
		log.info("카카오 로그인 시도");
		KakaoCodeRequest kakaoCodeRequest = new KakaoCodeRequest(code);
		return BaseResponse.success(200, kakaoService.kakaoLogin(kakaoCodeRequest, response, request), "카카오 로그인 성공");
	}

	@Operation(summary = "회원 정보 조회 API", description = "회원정보를 조회하는 API입니다.")
	@GetMapping("/")
	public BaseResponse<UserResponse> login(@RequestHeader("X-CODE") String userCode) throws IOException {
		return BaseResponse.success(200, userService.getByLoginUserCode(userCode), "회원정보 조회 성공");
	}

	@Operation(summary = "비밀번호 변경 API", description = "비밀번호를 변경하는 API입니다.")
	@PatchMapping("/pwd")
	public BaseResponse<ChangePasswordResponse> changePassword(@RequestHeader("X-CODE") String userCode,
		ChangePasswordRequest changePasswordRequest) {
		return BaseResponse.success(200, userService.changePassword(userCode, changePasswordRequest), "비밀번호 변경 성공");
	}

	@Operation(summary = "회원정보 변경 API", description = "회원정보를 변경하는 API입니다.")
	@PatchMapping("/")
	public BaseResponse<ModifyUserInfoResponse> modifyUserInfo(@RequestHeader("X-CODE") String userCode,
		ModifyUserInfoRequest modifyUserInfoRequest) {
		return BaseResponse.success(200, userService.modifyUserInfo(userCode, modifyUserInfoRequest), "유저 정보 변경 성공");
	}
}
