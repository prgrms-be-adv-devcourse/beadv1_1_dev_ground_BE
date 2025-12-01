package com.example.user.service;

import com.example.user.model.dto.request.KakaoCodeRequest;
import com.example.user.model.dto.response.LoginResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface KakaoService {
	LoginResponse kakaoLogin(KakaoCodeRequest kakaoCodeRequest, HttpServletResponse response, HttpServletRequest request);
}
