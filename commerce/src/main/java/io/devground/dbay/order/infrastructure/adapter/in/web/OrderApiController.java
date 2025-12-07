package io.devground.dbay.order.infrastructure.adapter.in.web;

import io.devground.core.model.web.BaseResponse;
import io.devground.dbay.order.application.service.OrderApplication;
import io.devground.dbay.order.domain.port.in.OrderUseCase;
import io.devground.dbay.order.domain.vo.OrderDescription;
import io.devground.dbay.order.domain.vo.ProductCode;
import io.devground.dbay.order.domain.vo.RoleType;
import io.devground.dbay.order.domain.vo.UserCode;
import io.devground.dbay.order.domain.vo.pagination.PageDto;
import io.devground.dbay.order.domain.vo.pagination.PageQuery;
import io.devground.dbay.order.domain.vo.pagination.SortSpec;
import io.devground.dbay.order.infrastructure.vo.OrderProductsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
            @RequestParam(defaultValue = "DESC") SortSpec.Direction dir
            ) {
        PageQuery pageQuery = new PageQuery(page, size, new SortSpec(sort, dir));
        return BaseResponse.success(
                200,
                orderApplication.getOrderLists(new UserCode(userCode), roleType, pageQuery),
                "주문 목록 조회 성공"
        );
    }
}
