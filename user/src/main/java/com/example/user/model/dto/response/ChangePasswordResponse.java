package com.example.user.model.dto.response;

import java.time.LocalDateTime;

public record ChangePasswordResponse(
	String userCode,              // 누가 변경했는지 (토큰 기반이면 생략도 가능)
	LocalDateTime changedAt
) {
}
