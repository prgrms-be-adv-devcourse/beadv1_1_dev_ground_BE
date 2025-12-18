package io.devground.dbay.order.infrastructure.model.persistence;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.vo.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "OrderItem")
public class OrderItemEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orderId", nullable = false)
    private OrderEntity orderEntity;

    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private String productCode;

    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private String sellerCode;

    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    private String productName;

    @Column(nullable = false)
    private Long productPrice;

    @Builder
    public OrderItemEntity(OrderEntity orderEntity, String productCode, String sellerCode, String productName, Long productPrice) {
        if (orderEntity == null) {
            throw ErrorCode.ORDER_NOT_FOUND.throwServiceException();
        }

        if (!StringUtils.hasText(productCode)) {
            throw ErrorCode.PRODUCT_NOT_FOUND.throwServiceException();
        }

        if (!StringUtils.hasText(sellerCode)) {
            throw ErrorCode.USER_NOT_FOUNT.throwServiceException();
        }

        if (!StringUtils.hasText(productName)) {
            throw ErrorCode.PRODUCT_NOT_FOUND.throwServiceException();
        }

        if (productPrice == null) {
            throw ErrorCode.AMOUNT_MUST_BE_POSITIVE.throwServiceException();
        }

        this.orderEntity = orderEntity;
        this.productCode = productCode;
        this.sellerCode = sellerCode;
        this.productName = productName;
        this.productPrice = productPrice;
    }

}
