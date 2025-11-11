package com.example.auth.jwt;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.auth.model.dto.CustomUserDetails;
import com.example.user.model.dto.request.LoginRequest;
import com.example.user.model.dto.response.LoginResponse;
import com.example.user.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.devground.core.model.vo.ErrorCode;
import io.devground.core.model.web.BaseResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;
	private final RedisService redisService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RedisService redisService) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.redisService = redisService;

		//필터 경로를 "/api/users/login"으로 설정
		setFilterProcessesUrl("/api/users/login");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {
		try {
			String requestMethod = request.getMethod();
			if (!requestMethod.equals("POST")) {
				throw ErrorCode.METHOD_NOT_ALLOWED.throwServiceException();
			}

			LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

			String email = loginRequest.email();
			String password = loginRequest.password();

			if (email.isEmpty() || password.isEmpty()) {
				throw ErrorCode.USER_NOT_FOUNT.throwServiceException();
			}

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
			return authenticationManager.authenticate(authToken);
		} catch (Exception e) {
			throw ErrorCode.INVALID_PASSWORD.throwServiceException();
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authenticaion) throws AuthenticationException, IOException {
		CustomUserDetails customUserDetails = (CustomUserDetails) authenticaion.getPrincipal();

		String email = customUserDetails.getUsername();
		String userCode = customUserDetails.getUserCode();

		Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();

		String role = auth.getAuthority();

		//중복 로그인 방지
		String login = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("refresh")) {
					login = cookie.getValue();
				}
			}

			if (login != null) {
				redisService.delete(login);
			}
		}

		//토큰생성
		String access = jwtUtil.createJwt("access", userCode, role, 3600000L);    //1시간
		String refresh = jwtUtil.createJwt("refresh", userCode, role, 604800000L);    //7일

		//refresh 토큰 저장
		redisService.save(refresh, userCode, Duration.ofDays(7));

		//응답설정
		response.setHeader("access", access);
		response.addCookie(createCookie("refresh", refresh));

		LoginResponse loginResponse = new LoginResponse(role);
		response.setStatus(HttpStatus.OK.value());
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(BaseResponse.success(200, loginResponse, "로그인 성공")));
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws AuthenticationException, IOException {
		response.setStatus(401);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(BaseResponse.fail(400, "로그인에 실패했습니다.")));
	}

	private Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24 * 60 * 60);
		cookie.setHttpOnly(true);
		cookie.setDomain("");
		cookie.setSecure(true);
		cookie.setAttribute("SameSite", "None");
		cookie.setPath("/");

		return cookie;
	}

}
