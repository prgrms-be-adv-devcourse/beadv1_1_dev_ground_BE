package io.devground.dbay.cart.infrastructure.adapter.in.web;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.cart.application.service.CartApplication;
import io.devground.dbay.cart.domain.vo.CartDescription;
import io.devground.dbay.cart.domain.vo.ProductCode;
import io.devground.dbay.cart.domain.vo.UserCode;
import io.devground.dbay.cart.infrastructure.adapter.in.vo.AddCartItemRequest;
import io.devground.dbay.cart.infrastructure.adapter.in.vo.AddCartItemResponse;
import io.devground.dbay.cart.infrastructure.adapter.in.vo.DeleteItemsByCartRequest;
import io.devground.dbay.cart.infrastructure.mapper.CartMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/commerce")
@Tag(name = "CommerceController")
public class CommerceApiController {

    private final CartApplication cartApplication;

    @PostMapping
    @Operation(summary = "장바구니 상품 추가", description = "장바구니에 상품을 추가합니다.")
    public BaseResponse<AddCartItemResponse> addItem(
            @RequestHeader("X-CODE") String userCode,
            @RequestBody AddCartItemRequest request
    ) {
        return BaseResponse.success(
                200,
                CartMapper.toAddCartItemResponse(cartApplication
                        .addCartItem(new UserCode(userCode), new ProductCode(request.productCode()))) ,
                "장바구니 상품 추가 성공"
        );
    }

    @GetMapping
    @Operation(summary = "장바구니 상품 조회", description = "장바구니에 상품을 조회합니다.")
    public BaseResponse<CartDescription> getItemsByCart(
            @RequestHeader("X-CODE") String userCode
    ) {
        return BaseResponse.success(
                200,
                cartApplication.getCartInfos(new UserCode(userCode)),
                "장바구니 조회 성공"
        );
    }

    @DeleteMapping("cart/delete/{productCode}")
    @Operation(summary = "장바구니 상품 삭제", description = "장바구니 상품을 삭제합니다.(개별)")
    public BaseResponse<Void> deleteItemByCart(
            @RequestHeader("X-CODE") String userCode,
            @PathVariable String productCode
    ) {
        cartApplication.removeCartItem(new UserCode(userCode), new ProductCode(productCode));
        return BaseResponse.success(
                204,
                "장바구니 상품 삭제 성공"
        );
    }

    @DeleteMapping("cart/deleteSel")
    @Operation(summary = "장바구니 상품 삭제(선택)", description = "장바구니 상품을 삭제합니다.(선택)")
    public BaseResponse<Void> deleteItemsByCart(
            @RequestHeader("X-CODE") String userCode,
            @RequestBody @Valid DeleteItemsByCartRequest request
            ) {
        List<ProductCode> productCodes = request
                .cartProductCodes()
                .stream()
                .map(ProductCode::new)
                .toList();

        cartApplication.removeCartItems(new UserCode(userCode), productCodes);

        return BaseResponse.success(
                204,
                "장바구니 선택 삭제 성공"
        );
    }

    @DeleteMapping("cart/deleteAll")
    @Operation(summary = "장바구니 상품 삭제(전체)", description = "장바구니 상품을 삭제합니다.(전체)")
    public BaseResponse<Void> deleteAllItemsByCart(
            @RequestHeader("X-CODE") String userCode
    ) {
        cartApplication.removeAllCartItems(new UserCode(userCode));

        return BaseResponse.success(
                204,
                "장바구니 전체 삭제 성공"
        );
    }

}
