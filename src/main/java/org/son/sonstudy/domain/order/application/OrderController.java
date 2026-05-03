package org.son.sonstudy.domain.order.application;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.SuccessCode;
import org.son.sonstudy.common.jwt.data.UserContext;
import org.son.sonstudy.domain.order.application.request.CheckoutRequest;
import org.son.sonstudy.domain.order.application.request.OrderHistoryRequest;
import org.son.sonstudy.domain.order.business.OrderService;
import org.son.sonstudy.domain.order.business.response.CheckoutResponse;
import org.son.sonstudy.domain.order.business.response.OrderHistoryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order", description = "주문 API - JWT 인증 필요")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 내역 조회", description = "로그인된 사용자의 주문 내역을 커서 기반 페이지네이션으로 조회합니다. 기본 페이지 크기는 10입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패 - JWT 토큰이 없거나 유효하지 않음")
    })
    @GetMapping
    public ResponseEntity<org.son.sonstudy.common.api.response.ApiResponse<OrderHistoryResponse>> getOrderHistory(
            @AuthenticationPrincipal UserContext userContext,
            @Valid @ModelAttribute OrderHistoryRequest request
    ) {
        OrderHistoryResponse response = orderService.getOrderHistory(
                userContext.userId(),
                request
        );

        return org.son.sonstudy.common.api.response.ApiResponse.success(SuccessCode.OK, response);
    }

    @Operation(summary = "주문 결제(체크아웃)", description = "상품 옵션을 선택하여 주문을 생성하고 결제를 처리합니다. 멱등성 키(idempotencyKey)로 중복 결제를 방지합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "주문 생성 및 결제 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패 - JWT 토큰이 없거나 유효하지 않음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품 옵션")
    })
    @PostMapping("/checkout")
    public ResponseEntity<org.son.sonstudy.common.api.response.ApiResponse<CheckoutResponse>> checkout(
            @AuthenticationPrincipal UserContext userContext,
            @RequestBody @Valid CheckoutRequest request
    ) {
        CheckoutResponse response = orderService.checkout(
                userContext.userId(),
                request
        );

        return org.son.sonstudy.common.api.response.ApiResponse.success(SuccessCode.CREATED , response);
    }
}
