package org.son.sonstudy.domain.order.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.SuccessCode;
import org.son.sonstudy.common.api.response.ApiResponse;
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

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<OrderHistoryResponse>> getOrderHistory(
            @AuthenticationPrincipal UserContext userContext,
            @Valid @ModelAttribute OrderHistoryRequest request
    ) {
        OrderHistoryResponse response = orderService.getOrderHistory(
                userContext.userId(),
                request
        );

        return ApiResponse.success(SuccessCode.OK, response);
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<CheckoutResponse>> checkout(
            @AuthenticationPrincipal UserContext userContext,
            @RequestBody @Valid CheckoutRequest request
    ) {
        CheckoutResponse response = orderService.checkout(
                userContext.userId(),
                request
        );

        return ApiResponse.success(SuccessCode.CREATED , response);
    }
}
