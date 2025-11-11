package com.example.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.jwt.JWTUtil;
import com.example.auth.service.ReissueService;

import io.devground.core.model.web.BaseResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReissueController {
	private final JWTUtil jwtUtil;
	private final ReissueService reissueService;

	@PostMapping("/api/users/reissue")
	public BaseResponse<?> reissue(HttpServletRequest request, HttpServletResponse response) {
		String refresh = null;

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if("refresh".equals(cookie.getName())) {
					refresh = cookie.getValue();
				}
			}
		}
		if(refresh == null) {
			return BaseResponse.fail(400, "refresh token null");
		}

		//expired check
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			//response status code
			return BaseResponse.fail(400, "refresh token expired");
		}

		// 토큰 재발급
		String newAccessToken = reissueService.reissueAccessToken(refresh);
		String newRefreshToken = reissueService.reissueRefreshToken(refresh);

		// 응답에 토큰 추가
		response.setHeader("access", newAccessToken);
		response.addCookie(createCookie("refresh", newRefreshToken));

		return BaseResponse.success(200, "토큰 재발급 성공");
	}

	private Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24*60*60);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setDomain("");
		cookie.setSecure(true);
		cookie.setAttribute("SameSite", "None");

		return cookie;
	}
}
