package io.devground.user.model.dto.request;

import jakarta.validation.constraints.Pattern;

public record ModifyUserInfoRequest(
	String nickname,

	@Pattern(
		regexp = "^010-\\d{4}-\\d{4}$",
		message = "전화번호 형식은 010-0000-0000입니다."
	)
	String phone,

	String address,
	String addressDetail
) {
}
