package org.son.sonstudy.domain.payment.business.pg;

import org.springframework.stereotype.Component;

@Component
public class FakePaymentGateway implements PaymentGateway {

    @Override
    public PaymentGatewayResult approve(PaymentApproveCommand command) {
        if (shouldFail(command.merchantUid())) {
            return PaymentGatewayResult.fail("FAKE_PG_DECLINED", "Fake PG 결제 실패");
        }

        String pgTransactionId = "FAKE-PG-" + Integer.toUnsignedString(command.merchantUid().hashCode());
        return PaymentGatewayResult.success(pgTransactionId);
    }

    private boolean shouldFail(String merchantUid) {
        return merchantUid != null && merchantUid.toUpperCase().endsWith("FAIL");
    }
}
