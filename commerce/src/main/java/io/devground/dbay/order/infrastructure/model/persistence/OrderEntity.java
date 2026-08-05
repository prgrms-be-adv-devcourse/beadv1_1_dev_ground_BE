package io.devground.dbay.order.infrastructure.model.persistence;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.vo.ErrorCode;

import io.devground.dbay.order.domain.vo.OrderStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "Orders")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OrderEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private String userCode;

    @Column(columnDefinition = "VARCHAR(100)")
    private String nickName;

    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    private String address;

    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    private String addressDetail;

    @Column(nullable = false)
    private Long totalAmount;

    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @OneToMany(mappedBy = "orderEntity")
    List<OrderItemEntity> orderItems = new ArrayList<>();

    @Builder
    public OrderEntity(String orderCode, String userCode, String nickName, String address, String addressDetail, Long totalAmount) {
        if (!StringUtils.hasText(userCode)) {
            throw ErrorCode.USER_NOT_FOUNT.throwServiceException();
        }

        if (!StringUtils.hasText(nickName)) {
            throw ErrorCode.USER_NOT_FOUNT.throwServiceException();
        }

        if (!StringUtils.hasText(address)) {
            throw ErrorCode.ADDRESS_NOT_FOUND.throwServiceException();
        }
        this.register(orderCode);
        this.userCode = userCode;
        this.nickName = nickName.trim();
        this.address = address;
        this.addressDetail = addressDetail;
        this.totalAmount = totalAmount;
    }

}
