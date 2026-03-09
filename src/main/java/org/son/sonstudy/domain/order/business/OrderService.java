package org.son.sonstudy.domain.order.business;

import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.exception.CustomException;
import org.son.sonstudy.domain.delivery.model.DeliveryStatus;
import org.son.sonstudy.domain.order.application.request.CheckoutRequest;
import org.son.sonstudy.domain.order.application.request.OrderHistoryRequest;
import org.son.sonstudy.domain.order.business.response.CheckoutResponse;
import org.son.sonstudy.domain.order.business.response.OrderHistoryResponse;
import org.son.sonstudy.domain.order.model.Order;
import org.son.sonstudy.domain.order.repository.OrderRepository;
import org.son.sonstudy.domain.order.repository.OrderRepositoryCustom;
import org.son.sonstudy.domain.payment.business.pg.PaymentApproveCommand;
import org.son.sonstudy.domain.payment.business.pg.PaymentGateway;
import org.son.sonstudy.domain.payment.business.pg.PaymentGatewayResult;
import org.son.sonstudy.domain.payment.model.Payment;
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
    public OrderHistoryResponse getOrderHistory(String userId, OrderHistoryRequest request) {
        int size = request.sizeOrDefault();
        List<OrderRepositoryCustom.OrderHistoryRow> rows = orderRepository.findOrderHistoryByCursor(
                userId,
                request.cursorOrderDate(),
                request.cursorOrderId(),
                size
        );

        boolean hasNext = rows.size() > size;
        List<OrderRepositoryCustom.OrderHistoryRow> contentRows = hasNext ? rows.subList(0, size) : rows;
        List<OrderHistoryResponse.OrderHistoryItem> content = contentRows.stream()
                .map(row -> OrderHistoryResponse.OrderHistoryItem.of(
                        row.orderId(),
                        row.orderedAt(),
                        row.orderStatus(),
                        row.productName(),
                        row.productImageUrl(),
                        row.size(),
                        row.amount(),
                        row.deliveryStatus() != null ? row.deliveryStatus() : DeliveryStatus.READY,
                        row.trackingNumber()
                ))
                .toList();

        LocalDateTime nextCursorOrderDate = null;
        String nextCursorOrderId = null;
        if (hasNext && !contentRows.isEmpty()) {
            OrderRepositoryCustom.OrderHistoryRow last = contentRows.get(contentRows.size() - 1);
            nextCursorOrderDate = last.orderedAt();
            nextCursorOrderId = last.orderId();
        }

        return OrderHistoryResponse.of(content, nextCursorOrderDate, nextCursorOrderId, hasNext);
    }

    @Transactional
    public CheckoutResponse checkout(String userId, CheckoutRequest request) {
        Payment existingPayment = paymentRepository.findByMerchantUid(request.idempotencyKey()).orElse(null);
        if (existingPayment != null) {
            return toResponse(existingPayment);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        ProductOption productOption = productOptionRepository.findById(request.productOptionId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        validateCheckoutRequest(userId, request, productOption);

        Payment payment = Payment.createRequested(
                request.idempotencyKey(),
                request.paymentMethod(),
                request.amount()
        );
        paymentRepository.save(payment);

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
