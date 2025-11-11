package com.example.auth.jwt;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;

import org.springframework.web.filter.GenericFilterBean;

import com.example.user.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.devground.core.model.web.BaseResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {
	private final JWTUtil jwtUtil;
	private final RedisService redisService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {
		doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		IOException,
		ServletException {

		//경로, 메서드 확인
		String requestUri = request.getRequestURI();
		if (!requestUri.matches("^\\/api/users/logout$")) {
			filterChain.doFilter(request, response);
			return;
		}

		String requestMethod = request.getMethod();
		if (!requestMethod.equals("POST")) {
			filterChain.doFilter(request, response);
			return;
		}

		//get refresh token
		String refresh = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("refresh")) {
				refresh = cookie.getValue();
			}
		}

		//refresh null check
		if (refresh == null) {
			log.error("refresh 토큰이 없습니다.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		//만료 확인
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			log.error("refresh 토큰이 만료되었습니다.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		//토큰이 refresh 토큰인지 확인
		String category = jwtUtil.getCategory(refresh);
		if (!category.equals("refresh")) {
			log.error("refresh 토큰이 아닙니다.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		//유효기간이 남아있는 access 토큰을 남아있는 기간만큼 redis에 저장
		try {
			if (!jwtUtil.isExpired(request.getHeader("access"))) {
				String accessToken = request.getHeader("access");
				Date expiration = jwtUtil.getExpiration(accessToken);
				Date now = new Date();
				Duration duration = Duration.between(now.toInstant(), expiration.toInstant());
				redisService.save(accessToken, "access", duration);
			}
		} catch (ExpiredJwtException e) {
			log.info("access 토큰 만료");
		}

		//로그아웃 진행
		//Refresh 토큰 DB에서 제거
		redisService.delete(refresh);

		//refresh 토큰 cookie 값 0
		Cookie cookie = new Cookie("refresh", null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		cookie.setDomain("");
		cookie.setSecure(true);
		cookie.setAttribute("SameSite", "None");
		response.addCookie(cookie);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write(objectMapper.writeValueAsString(BaseResponse.success(200, "로그아웃 성공")));

	}
}
