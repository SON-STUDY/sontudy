package org.son.sonstudy.domain.order.business.response;

import org.son.sonstudy.domain.delivery.model.DeliveryStatus;
import org.son.sonstudy.domain.order.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderHistoryResponse(
        List<OrderHistoryItem> content,
        LocalDateTime nextCursorOrderDate,
        String nextCursorOrderId,
        boolean hasNext
) {
    public static OrderHistoryResponse of(
            List<OrderHistoryItem> content,
            LocalDateTime nextCursorOrderDate,
            String nextCursorOrderId,
            boolean hasNext
    ) {
        return new OrderHistoryResponse(content, nextCursorOrderDate, nextCursorOrderId, hasNext);
    }

    public record OrderHistoryItem(
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
        public static OrderHistoryItem of(
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
            return new OrderHistoryItem(
                    orderId,
                    orderedAt,
                    orderStatus,
                    productName,
                    productImageUrl,
                    size,
                    amount,
                    deliveryStatus,
                    trackingNumber
            );
        }
    }
}
