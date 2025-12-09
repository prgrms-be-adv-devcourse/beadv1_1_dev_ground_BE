package io.devground.user.mapper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.devground.core.model.entity.RoleType;
import io.devground.user.model.dto.request.KakaoUserRequest;
import io.devground.user.model.dto.request.UserRequest;
import io.devground.user.model.entity.User;

public class UserMapper {
	public static User toEntity(UserRequest userRequest, BCryptPasswordEncoder bCryptPasswordEncoder, String userCode) {
		return User.builder()
			.email(userRequest.email())
			.name(userRequest.name())
			.code(userCode)
			.password(bCryptPasswordEncoder.encode(userRequest.password()))
			.nickname(userRequest.nickname())
			.phone(userRequest.phone())
			.address(userRequest.address())
			.addressDetail(userRequest.addressDetail())
			.role(RoleType.USER)
			.profileImage("")
			.build();
	}

	public static User kakaoToEntity(KakaoUserRequest kakaoUserRequest, Long oauthId, BCryptPasswordEncoder bCryptPasswordEncoder) {
		return User.builder()
			.email(kakaoUserRequest.email())
			.name(kakaoUserRequest.name())
			.password(bCryptPasswordEncoder.encode(kakaoUserRequest.password()))
			.nickname(kakaoUserRequest.nickname())
			.phone(kakaoUserRequest.phone())
			.address(kakaoUserRequest.address())
			.addressDetail(kakaoUserRequest.addressDetail())
			.role(RoleType.USER)
			.oauthId(oauthId)
			.build();
	}
}
