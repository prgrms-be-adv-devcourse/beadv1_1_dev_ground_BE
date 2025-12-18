package io.devground.chat.model.dto.response;

public record UserResponse(
        String name,
        String email,
        String nickname,
        String phone,
        String address,
        String addressDetail,
        String profileImage
) {
}
