package io.devground.dbay.order.application.port.out.user;

import io.devground.dbay.order.application.vo.UserInfo;
import io.devground.dbay.order.domain.vo.UserCode;

public interface OrderUserPort {
    UserInfo getUserInfo(UserCode userCode);
}
