package org.son.sonstudy.domain.payment.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.son.sonstudy.domain.order.model.Order;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Table(name = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    @Id
    @Tsid
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false, unique = true, length = 100)
    private String merchantUid;

    @Column(length = 100)
    private String pgTransactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethod method;

    @Column(nullable = false)
    private Long amount;

    @Column(length = 50)
    private String failureCode;

    @Column(length = 255)
    private String failureMessage;

    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime failedAt;

    @Builder
    private Payment(Order order, String merchantUid, PaymentMethod method, Long amount) {
        this.order = order;
        this.merchantUid = merchantUid;
        this.method = method;
        this.amount = amount;
        this.status = PaymentStatus.REQUESTED;
        this.requestedAt = LocalDateTime.now();
    }

    public static Payment createRequested(String merchantUid, PaymentMethod method, Long amount) {
        return Payment.builder()
                .merchantUid(merchantUid)
                .method(method)
                .amount(amount)
                .build();
    }

    public static Payment createRequested(Order order, String merchantUid, PaymentMethod method, Long amount) {
        return Payment.builder()
                .order(order)
                .merchantUid(merchantUid)
                .method(method)
                .amount(amount)
                .build();
    }

    public void attachOrder(Order order) {
        this.order = Objects.requireNonNull(order, "order must not be null");
    }

    public void markPaid(String pgTransactionId) {
        this.status = PaymentStatus.PAID;
        this.pgTransactionId = pgTransactionId;
        this.approvedAt = LocalDateTime.now();
        this.failedAt = null;
        this.failureCode = null;
        this.failureMessage = null;
    }

    public void markFailed(String failureCode, String failureMessage) {
        this.status = PaymentStatus.FAILED;
        this.failedAt = LocalDateTime.now();
        this.failureCode = failureCode;
        this.failureMessage = failureMessage;
        this.approvedAt = null;
    }

    public void recreateRequested(PaymentMethod method, Long amount) {
        this.status = PaymentStatus.REQUESTED;
        this.method = method;
        this.amount = amount;
        this.requestedAt = LocalDateTime.now();
        this.approvedAt = null;
        this.failedAt = null;
        this.failureCode = null;
        this.failureMessage = null;
        this.pgTransactionId = null;
        this.order = null;
    }
}
