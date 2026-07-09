package org.son.sonstudy.domain.delivery.business;

import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.exception.CustomException;
import org.son.sonstudy.domain.delivery.application.request.DeliveryShipmentRequest;
import org.son.sonstudy.domain.delivery.business.response.DeliveryShipmentResponse;
import org.son.sonstudy.domain.delivery.business.tracker.DeliveryTracker;
import org.son.sonstudy.domain.delivery.business.tracker.DeliveryTrackingResult;
import org.son.sonstudy.domain.delivery.model.Delivery;
import org.son.sonstudy.domain.delivery.model.DeliveryStatus;
import org.son.sonstudy.domain.order.model.Order;
import org.son.sonstudy.domain.order.model.OrderStatus;
import org.son.sonstudy.domain.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final OrderRepository orderRepository;
    private final DeliveryTracker deliveryTracker;

    @Transactional
    public DeliveryShipmentResponse registerShipment(String orderId, DeliveryShipmentRequest request) {
        Order order = orderRepository.findWithDeliveryById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (order.getStatus() != OrderStatus.PURCHASED) {
            throw new CustomException(ErrorCode.INVALID_ORDER_STATUS);
        }

        Delivery delivery = order.getDelivery();
        delivery.registerShipment(request.courierCompany(), request.trackingNumber());

        return DeliveryShipmentResponse.from(delivery);
    }

    @Transactional
    public DeliveryShipmentResponse updateDeliveryStatus(String orderId) {
        Order order = orderRepository.findWithDeliveryById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Delivery delivery = order.getDelivery();
        if (delivery.getStatus() != DeliveryStatus.DELIVERING) {
            throw new CustomException(ErrorCode.INVALID_DELIVERY_STATUS);
        }

        DeliveryTrackingResult result = deliveryTracker.track(
                delivery.getCourierCompany(),
                delivery.getTrackingNumber()
        );

        if (result.completed()) {
            delivery.complete();
        }

        return DeliveryShipmentResponse.from(delivery);
    }
}
