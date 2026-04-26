package org.son.sonstudy.domain.delivery.application.request;

import jakarta.validation.constraints.NotBlank;

public record DeliveryShipmentRequest(
        @NotBlank(message = "택배사는 필수입니다.")
        String courierCompany,

        @NotBlank(message = "송장번호는 필수입니다.")
        String trackingNumber
) {
}
