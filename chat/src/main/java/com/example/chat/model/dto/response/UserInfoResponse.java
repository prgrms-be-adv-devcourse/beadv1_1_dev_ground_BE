package com.example.chat.model.dto.response;

public record UserInfoResponse(
        String name,
        String email,
        String nickname,
        String phone,
        String address,
        String addressDetail,
        String profileImage
) {
}
