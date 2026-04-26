package org.son.sonstudy.domain.delivery.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.SuccessCode;
import org.son.sonstudy.common.api.response.ApiResponse;
import org.son.sonstudy.domain.delivery.application.request.DeliveryShipmentRequest;
import org.son.sonstudy.domain.delivery.business.DeliveryService;
import org.son.sonstudy.domain.delivery.business.response.DeliveryShipmentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PutMapping("/orders/{orderId}/shipment")
    public ResponseEntity<ApiResponse<DeliveryShipmentResponse>> registerShipment(
            @PathVariable String orderId,
            @RequestBody @Valid DeliveryShipmentRequest request
    ) {
        DeliveryShipmentResponse response = deliveryService.registerShipment(orderId, request);
        return ApiResponse.success(SuccessCode.OK, response);
    }
}
