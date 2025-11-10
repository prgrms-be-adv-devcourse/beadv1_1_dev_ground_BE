package com.example.user.model.dto.request;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.user.model.entity.RoleType;
import com.example.user.model.entity.User;

public record UserRequest(
	String name,
	String email,
	String password,
	String nickname,
	String phone,
	String address,
	String addressDetail
) {
}