package io.devground.dbay.cart.infrastructure.model.persistence;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cartItem")
public class CartItemEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cartId", nullable = false)
    private CartEntity cartEntity;

    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private String productCode;

    @Builder
    public CartItemEntity(CartEntity cartEntity, String productCode) {
        if (cartEntity == null) {
            throw new ServiceException(ErrorCode.CART_NOT_FOUND);
        }

        if (!StringUtils.hasText(productCode)) {
            throw ErrorCode.CART_NOT_FOUND.throwServiceException();
        }

        this.cartEntity = cartEntity;
        this.productCode = productCode;
    }
}
