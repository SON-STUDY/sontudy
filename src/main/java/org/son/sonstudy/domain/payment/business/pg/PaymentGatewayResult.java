package org.son.sonstudy.domain.payment.business.pg;

public record PaymentGatewayResult(
        boolean success,
        String pgTransactionId,
        String failureCode,
        String failureMessage
) {
    public static PaymentGatewayResult success(String pgTransactionId) {
        return new PaymentGatewayResult(true, pgTransactionId, null, null);
    }

    public static PaymentGatewayResult fail(String failureCode, String failureMessage) {
        return new PaymentGatewayResult(false, null, failureCode, failureMessage);
    }
}
