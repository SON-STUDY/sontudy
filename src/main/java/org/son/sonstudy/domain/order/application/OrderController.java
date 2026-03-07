package org.son.sonstudy.domain.order.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.response.ApiResponse;
import org.son.sonstudy.common.jwt.data.UserContext;
import org.son.sonstudy.domain.order.application.request.CheckoutRequest;
import org.son.sonstudy.domain.order.business.OrderService;
import org.son.sonstudy.domain.order.business.response.CheckoutResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.son.sonstudy.common.api.code.SuccessCode;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

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
