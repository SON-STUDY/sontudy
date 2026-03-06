package org.son.sonstudy.domain.payment.business.pg;

public interface PaymentGateway {
    PaymentGatewayResult approve(PaymentApproveCommand command);
}
