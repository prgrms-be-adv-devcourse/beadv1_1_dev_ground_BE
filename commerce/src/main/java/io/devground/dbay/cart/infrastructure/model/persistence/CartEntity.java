package io.devground.dbay.cart.infrastructure.model.persistence;

import io.devground.core.model.entity.BaseEntity;
import io.devground.core.model.vo.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart")
public class CartEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private String userCode;

    @OneToMany(mappedBy = "cartEntity")
    List<CartItemEntity> cartItems = new ArrayList<>();

    @Builder
    public CartEntity(String cartCode, String userCode) {
        if (!StringUtils.hasText(userCode)) {
            throw ErrorCode.CART_NOT_FOUND.throwServiceException();
        }
        this.register(cartCode);
        this.userCode = userCode;
    }
}
