package io.devground.user.service;

import io.devground.user.model.dto.request.KakaoCodeRequest;
import io.devground.user.model.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface KakaoService {
	LoginResponse kakaoLogin(KakaoCodeRequest kakaoCodeRequest, HttpServletResponse response, HttpServletRequest request);
}
