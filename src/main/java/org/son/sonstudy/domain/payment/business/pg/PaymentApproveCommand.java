package org.son.sonstudy.domain.payment.business.pg;

import org.son.sonstudy.domain.payment.model.PaymentMethod;

public record PaymentApproveCommand(
        String merchantUid,
        PaymentMethod paymentMethod,
        Long amount
) {
}
