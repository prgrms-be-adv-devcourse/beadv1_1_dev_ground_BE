package com.example.auth.service;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.example.auth.jwt.JWTUtil;
import com.example.user.service.RedisService;

import io.devground.core.model.vo.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReissueService {
	private final JWTUtil jwtUtil;
	private final RedisService redisService;

	public String reissueAccessToken(String refreshToken) {
		validateRefreshToken(refreshToken);

		String userCode = jwtUtil.getUserCode(refreshToken);
		String role = jwtUtil.getRole(refreshToken);


		return jwtUtil.createJwt("access", userCode, role, 3600000L);
	}

	public String reissueRefreshToken(String refreshToken) {
		validateRefreshToken(refreshToken);

		String userCode = jwtUtil.getUserCode(refreshToken);
		String role = jwtUtil.getRole(refreshToken);


		String newRefreshToken = jwtUtil.createJwt("refresh", userCode, role, 3600000L);
		redisService.delete(refreshToken);
		redisService.save(newRefreshToken, userCode, Duration.ofDays(7));

		return newRefreshToken;
	}

	public void validateRefreshToken(String refreshToken){
		if(refreshToken == null
			|| !redisService.exists(refreshToken)
			|| !"refresh".equals(jwtUtil.getCategory(refreshToken))) {

			throw ErrorCode.EMPTY_REFRESH_TOKEN.throwServiceException();
		}

		if(jwtUtil.isExpired(refreshToken)) {
			throw ErrorCode.EXPIRED_REFRESH_TOKEN.throwServiceException();
		}
	}
}
