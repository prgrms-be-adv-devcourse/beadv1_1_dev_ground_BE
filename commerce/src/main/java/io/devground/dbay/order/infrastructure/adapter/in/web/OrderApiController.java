package io.devground.dbay.order.infrastructure.adapter.in.web;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.order.application.service.OrderApplication;
import io.devground.dbay.order.domain.vo.*;
import io.devground.dbay.order.domain.vo.pagination.PageDto;
import io.devground.dbay.order.domain.vo.pagination.PageQuery;
import io.devground.dbay.order.domain.vo.pagination.SortSpec;
import io.devground.dbay.order.infrastructure.vo.OrderProductsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/commerce/order")
@Tag(name = "OrderController")
public class OrderApiController {

    private final OrderApplication orderApplication;

    @PostMapping("/{productCode}")
    @Operation(summary = "주문 단건 생성", description = "단건 주문을 생성합니다.")
    public BaseResponse<Void> createSingleOrder(
            @RequestHeader("X-CODE") String userCode,
            @PathVariable String productCode
    ) {
        orderApplication.createOrderByOne(new UserCode(userCode), new ProductCode(productCode));

        return BaseResponse.success(
                204,
                "주문 생성 완료"
        );
    }

    @PostMapping
    @Operation(summary = "주문 다건 생성", description = "다건의 주문을 생성합니다.")
    public BaseResponse<Void> createSelectedOrder(
            @RequestHeader("X-CODE") String userCode,
            @RequestBody OrderProductsRequest request
    ) {
        List<ProductCode> productCodes = request.productCodes().stream()
                        .map(ProductCode::new)
                        .toList();

        orderApplication.createOrderBySelected(new UserCode(userCode), productCodes);

        return BaseResponse.success(204, "주문 생성 완료");
    }

    @GetMapping
    @Operation(summary = "주문 조회", description = "주문 목록 조회")
    public BaseResponse<PageDto<OrderDescription>> getOrders(
            @RequestHeader("X-CODE") String userCode,
            @RequestHeader("ROLE") RoleType roleType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") SortSpec.Direction dir,
            @RequestParam(defaultValue = "ALL") OrderStatus orderStatus
            ) {
        PageQuery pageQuery = new PageQuery(page, size, new SortSpec(sort, dir));
        return BaseResponse.success(
                200,
                orderApplication.getOrderLists(new UserCode(userCode), roleType, pageQuery, orderStatus),
                "주문 목록 조회 성공"
        );
    }

    @GetMapping("/{orderCode}")
    @Operation(summary = "주문 상세 조회", description = "주문 상세 조회")
    public BaseResponse<OrderDetailDescription> getOrderDetail(
            @RequestHeader("X-CODE") String userCode,
            @PathVariable String orderCode
    ) {
        return BaseResponse.success(
                200,
                orderApplication.getOrderDetail(new UserCode(userCode), new OrderCode(orderCode)),
                "주문 상세 조회 성공"
        );
    }

    @PatchMapping("/{orderCode}")
    @Operation(summary = "주문 취소", description = "주문 취소")
    public BaseResponse<Void> cancelOrder(
            @RequestHeader("X-CODE") String userCode,
            @PathVariable String orderCode
    ) {
        orderApplication.cancelOrder(new UserCode(userCode), new OrderCode(orderCode));
        return BaseResponse.success(
                204,
                "주문 취소 완료"
        );
    }

    @PatchMapping("/confirm/{orderCode}")
    @Operation(summary = "주문 구매 확정", description = "주문 구매 확정")
    public BaseResponse<Void> confirmOrder(
            @RequestHeader("X-CODE") String userCode,
            @PathVariable String orderCode
    ) {
        orderApplication.confirmOrder(new UserCode(userCode), new OrderCode(orderCode));

        return BaseResponse.success(
                204,
                "구매 확정 완료"
        );
    }

    @GetMapping("/unsettled-items")
    @Operation(summary = "정산 위한 주문 정보 조회", description = "주문 완료된 주문들에 대해 정산처리하기 위함")
    public BaseResponse<PageDto<UnsettledOrderItemResponse>> getUnsettledOrderItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "1000") int size
    ) {
        PageQuery pageQuery = new PageQuery(page, size, new SortSpec("id", SortSpec.Direction.ASC));
        return BaseResponse.success(
                200,
                orderApplication.getUnsettledOrderItems(pageQuery),
                "정산 처리를 위한 주문 정보 조회 완료"
        );
    }
}
