package com.example.user.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record KakaoUserRequest(
	@NotBlank
	String name,

	@Email
	String email,

	@NotBlank
	String password,

	@NotBlank
	String nickname,

	Long oauthId,

	@NotBlank(message = "전화번호는 필수입니다.")
	@Pattern(
		regexp = "^010-\\d{4}-\\d{4}$",
		message = "전화번호 형식은 010-0000-0000입니다."
	)
	String phone
) {
}
