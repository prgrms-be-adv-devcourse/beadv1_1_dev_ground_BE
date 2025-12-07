package io.devground.dbay.order.infrastructure.adapter.out.user;

import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.order.application.port.out.user.OrderUserPort;
import io.devground.dbay.order.application.vo.UserInfo;
import io.devground.dbay.order.domain.vo.UserCode;
import io.devground.dbay.order.infrastructure.mapper.OrderMapper;
import io.devground.dbay.order.infrastructure.vo.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderUserFeignAdapter implements OrderUserPort {

    private final UserFeignClient userFeignClient;

    @Override
    public UserInfo getUserInfo(UserCode userCode) {
        if (userCode == null) {
            throw ErrorCode.CODE_INVALID.throwServiceException();
        }

        UserResponse userInfo = userFeignClient.login(userCode.value()).throwIfNotSuccess().data();

        if (userInfo == null) {
            throw ErrorCode.USER_NOT_FOUNT.throwServiceException();
        }

        return OrderMapper.toUserInfo(userInfo);
    }
}
