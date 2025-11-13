package com.example.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.model.dto.request.EmailCertificationRequest;
import com.example.user.model.dto.request.UserRequest;
import com.example.user.service.UserService;

import io.devground.core.model.web.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "UserController")
public class UserController {
	private final UserService userService;

	@Operation(summary = "인증메일 전송 API", description = "인증 메일을 전송합니다.")
	@PostMapping("/send")
	public BaseResponse<String> sendCertificateEmail(@RequestBody UserRequest userRequest) {
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
	public BaseResponse<String> deleteUser(@RequestHeader("X-CODE") String userCode){
		userService.requestDeleteUser(userCode);

		return BaseResponse.success(200, "회원탈퇴 성공");
	}
}
