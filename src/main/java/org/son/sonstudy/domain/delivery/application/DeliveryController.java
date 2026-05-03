package org.son.sonstudy.domain.delivery.application;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.SuccessCode;
import org.son.sonstudy.domain.delivery.application.request.DeliveryShipmentRequest;
import org.son.sonstudy.domain.delivery.business.DeliveryService;
import org.son.sonstudy.domain.delivery.business.response.DeliveryShipmentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Delivery", description = "배송 API")
@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(summary = "배송 정보 등록", description = "주문에 대한 택배사와 송장번호를 등록합니다. 이미 출고 처리된 주문에는 재등록할 수 없습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송 정보 등록 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패 또는 현재 배송 상태에서 수행 불가"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 주문")
    })
    @PutMapping("/orders/{orderId}/shipment")
    public ResponseEntity<org.son.sonstudy.common.api.response.ApiResponse<DeliveryShipmentResponse>> registerShipment(
            @Parameter(description = "주문 ID") @PathVariable String orderId,
            @RequestBody @Valid DeliveryShipmentRequest request
    ) {
        DeliveryShipmentResponse response = deliveryService.registerShipment(orderId, request);
        return org.son.sonstudy.common.api.response.ApiResponse.success(SuccessCode.OK, response);
    }
}
