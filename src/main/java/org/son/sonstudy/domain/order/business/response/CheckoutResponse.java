package org.son.sonstudy.domain.order.business.response;

import org.son.sonstudy.domain.order.model.OrderStatus;
import org.son.sonstudy.domain.payment.model.PaymentStatus;

public record CheckoutResponse(
        String orderId,
        String paymentId,
        OrderStatus orderStatus,
        PaymentStatus paymentStatus,
        String failureCode,
        String failureMessage
) {
}
