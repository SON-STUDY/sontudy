package org.son.sonstudy.domain.order.repository;

import org.son.sonstudy.domain.delivery.model.DeliveryStatus;
import org.son.sonstudy.domain.order.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepositoryCustom {
    List<OrderHistoryRow> findOrderHistoryByCursor(
            String userId,
            LocalDateTime cursorOrderDate,
            String cursorOrderId,
            int size
    );

    record OrderHistoryRow(
            String orderId,
            LocalDateTime orderedAt,
            OrderStatus orderStatus,
            String productName,
            String productImageUrl,
            int size,
            long amount,
            DeliveryStatus deliveryStatus,
            String trackingNumber
    ) {
    }
}
