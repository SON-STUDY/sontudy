package org.son.sonstudy.domain.order.business;

import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.aop.annotation.Loggable;
import org.son.sonstudy.common.aop.annotation.LogCategory;
import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.exception.CustomException;
import org.son.sonstudy.domain.order.application.request.CheckoutRequest;
import org.son.sonstudy.domain.order.application.request.OrderHistoryRequest;
import org.son.sonstudy.domain.order.business.response.CheckoutResponse;
import org.son.sonstudy.domain.order.business.response.OrderHistoryResponse;
import org.son.sonstudy.domain.order.model.Order;
import org.son.sonstudy.domain.order.repository.OrderRepository;
import org.son.sonstudy.domain.payment.business.pg.PaymentApproveCommand;
import org.son.sonstudy.domain.payment.business.pg.PaymentGateway;
import org.son.sonstudy.domain.payment.business.pg.PaymentGatewayResult;
import org.son.sonstudy.domain.payment.model.Payment;
import org.son.sonstudy.domain.payment.model.PaymentStatus;
import org.son.sonstudy.domain.payment.repository.PaymentRepository;
import org.son.sonstudy.domain.product.model.ProductOption;
import org.son.sonstudy.domain.product.model.ProductStatus;
import org.son.sonstudy.domain.product.repository.ProductOptionRepository;
import org.son.sonstudy.domain.user.model.User;
import org.son.sonstudy.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductOptionRepository productOptionRepository;

    @Transactional(readOnly = true)
    @Loggable(category = LogCategory.ORDER)
    public OrderHistoryResponse getOrderHistory(String userId, OrderHistoryRequest request) {
        int size = request.sizeOrDefault();
        List<OrderHistoryResponse.OrderHistoryItem> orderHistories = orderRepository.findOrderHistoryByCursor(
                userId,
                request.cursorOrderDate(),
                request.cursorOrderId(),
                size
        );
        
        boolean hasNext = orderHistories.size() > size;
        List<OrderHistoryResponse.OrderHistoryItem> orderHistoryRows = hasNext ? orderHistories.subList(0, size) : orderHistories;

        LocalDateTime nextCursorOrderDate = null;
        String nextCursorOrderId = null;
        if (hasNext && !orderHistoryRows.isEmpty()) {
            OrderHistoryResponse.OrderHistoryItem last = orderHistoryRows.get(orderHistoryRows.size() - 1);
            nextCursorOrderDate = last.orderedAt();
            nextCursorOrderId = last.orderId();
        }

        return OrderHistoryResponse.of(orderHistoryRows, nextCursorOrderDate, nextCursorOrderId, hasNext);
    }

    @Transactional
    @Loggable(category = LogCategory.ORDER)
    public CheckoutResponse checkout(String userId, CheckoutRequest request) {
        Payment existingPayment = paymentRepository.findByMerchantUid(request.idempotencyKey()).orElse(null);
        if (isAlreadyProcessed(existingPayment)) {
            return toResponse(existingPayment);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        ProductOption productOption = productOptionRepository.findById(request.productOptionId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        validateCheckoutRequest(userId, request, productOption);

        Payment payment = getOrCreatePayment(existingPayment, request);
        paymentRepository.save(payment);

        return processPaymentApproval(user, productOption, payment, request);
    }

    private boolean isAlreadyProcessed(Payment payment) {
        return payment != null && payment.getStatus() != PaymentStatus.FAILED;
    }

    private Payment getOrCreatePayment(Payment existingPayment, CheckoutRequest request) {
        if (existingPayment != null) {
            existingPayment.recreateRequested(request.paymentMethod(), request.amount());
            return existingPayment;
        }
        return Payment.createRequested(
                request.idempotencyKey(),
                request.paymentMethod(),
                request.amount()
        );
    }

    private CheckoutResponse processPaymentApproval(User user, ProductOption productOption, Payment payment, CheckoutRequest request) {
        PaymentGatewayResult gatewayResult = paymentGateway.approve(new PaymentApproveCommand(
                request.idempotencyKey(),
                request.paymentMethod(),
                request.amount()
        ));

        if (!gatewayResult.success()) {
            payment.markFailed(gatewayResult.failureCode(), gatewayResult.failureMessage());
            paymentRepository.save(payment);
            return toResponse(payment);
        }

        Order order = Order.createPurchased(user, productOption, Math.toIntExact(request.amount()));
        orderRepository.save(order);

        payment.attachOrder(order);
        payment.markPaid(gatewayResult.pgTransactionId());
        productOption.decreaseStock(1);
        paymentRepository.save(payment);

        return toResponse(payment);
    }

    private CheckoutResponse toResponse(Payment payment) {
        Order order = payment.getOrder();
        return new CheckoutResponse(
                order != null ? order.getId() : null,
                payment.getId(),
                order != null ? order.getStatus() : null,
                payment.getStatus(),
                payment.getFailureCode(),
                payment.getFailureMessage()
        );
    }

    private void validateCheckoutRequest(String userId, CheckoutRequest request, ProductOption productOption) {
        if (request.amount() != productOption.getCost()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        if (productOption.getStatus() != ProductStatus.ON_SALE || productOption.getStock() < 1) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        if (orderRepository.existsByUserIdAndProductId(userId, productOption.getProduct().getId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }
}
