package org.son.sonstudy.domain.order.application.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.son.sonstudy.domain.payment.model.PaymentMethod;

public record CheckoutRequest(
        @NotBlank String productOptionId,
        @NotNull PaymentMethod paymentMethod,
        @Positive Long amount,
        @NotBlank String idempotencyKey
) {
}
