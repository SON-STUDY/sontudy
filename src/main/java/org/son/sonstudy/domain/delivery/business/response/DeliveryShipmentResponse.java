package org.son.sonstudy.domain.delivery.business.response;

import org.son.sonstudy.domain.delivery.model.Delivery;
import org.son.sonstudy.domain.delivery.model.DeliveryStatus;

public record DeliveryShipmentResponse(
        String deliveryId,
        String courierCompany,
        String trackingNumber,
        DeliveryStatus status
) {
    public static DeliveryShipmentResponse from(Delivery delivery) {
        return new DeliveryShipmentResponse(
                delivery.getId(),
                delivery.getCourierCompany(),
                delivery.getTrackingNumber(),
                delivery.getStatus()
        );
    }
}
