package io.devground.dbay.order.infrastructure.vo;

public record UserResponse (
        String name,
        String email,
        String nickname,
        String phone,
        String address,
        String addressDetail
) {
}
