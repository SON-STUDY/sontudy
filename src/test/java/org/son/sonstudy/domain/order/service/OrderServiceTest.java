package org.son.sonstudy.domain.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.exception.CustomException;
import org.son.sonstudy.domain.order.application.request.CheckoutRequest;
import org.son.sonstudy.domain.order.application.request.OrderHistoryRequest;
import org.son.sonstudy.domain.order.business.OrderService;
import org.son.sonstudy.domain.order.business.response.CheckoutResponse;
import org.son.sonstudy.domain.order.business.response.OrderHistoryResponse;
import org.son.sonstudy.domain.order.model.Order;
import org.son.sonstudy.domain.order.model.OrderStatus;
import org.son.sonstudy.domain.order.repository.OrderRepository;
import org.son.sonstudy.domain.payment.model.Payment;
import org.son.sonstudy.domain.payment.model.PaymentMethod;
import org.son.sonstudy.domain.payment.model.PaymentStatus;
import org.son.sonstudy.domain.payment.repository.PaymentRepository;
import org.son.sonstudy.domain.product.model.Product;
import org.son.sonstudy.domain.product.model.ProductCategory;
import org.son.sonstudy.domain.product.model.ProductOption;
import org.son.sonstudy.domain.product.model.ProductStatus;
import org.son.sonstudy.domain.product.model.submodel.Color;
import org.son.sonstudy.domain.product.model.submodel.ColorRepository;
import org.son.sonstudy.domain.product.model.submodel.ProductImage;
import org.son.sonstudy.domain.product.repository.ProductOptionRepository;
import org.son.sonstudy.domain.product.repository.ProductRepository;
import org.son.sonstudy.domain.user.model.Role;
import org.son.sonstudy.domain.user.model.User;
import org.son.sonstudy.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class OrderServiceTest {

    @Autowired private OrderService orderService;

    @Autowired private UserRepository userRepository;

    @Autowired private OrderRepository orderRepository;
    @Autowired private PaymentRepository paymentRepository;

    @Autowired private ProductRepository productRepository;

    @Autowired private ProductOptionRepository productOptionRepository;

    @Autowired private ColorRepository colorRepository;

    private ProductOption defaultProductOption;
    private User defaultUser;
    private int userSequence;

    @BeforeEach
    void setUp() {
        userSequence = 0;
        defaultProductOption = createProductOption();
        defaultUser = createUser("default");
    }

    @Nested
    class 주문을_생성할_때 {

        @Nested
        class 유효한_요청이면 {

            @Test
            void 주문과_결제가_정상_처리된다() {
                // given
                String idempotencyKey = "checkout-success";
                CheckoutRequest request = createCheckoutRequest(idempotencyKey);

                // when
                CheckoutResponse response = orderService.checkout(defaultUser.getId(), request);

                // then
                assertThat(response.orderId()).isNotNull();
                assertThat(response.paymentId()).isNotNull();
                assertThat(response.orderStatus()).isEqualTo(OrderStatus.PURCHASED);
                assertThat(response.paymentStatus()).isEqualTo(PaymentStatus.PAID);
                assertThat(response.failureCode()).isNull();
                assertThat(response.failureMessage()).isNull();

                assertThat(orderRepository.count()).isEqualTo(1); // 주문 저장 검증
                Order savedOrder = orderRepository.findById(response.orderId()).orElseThrow();
                assertThat(savedOrder.getUser().getId()).isEqualTo(defaultUser.getId());
                assertThat(savedOrder.getProductOption().getId()).isEqualTo(defaultProductOption.getId());
                assertThat(savedOrder.getCost()).isEqualTo(defaultProductOption.getCost());
                assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PURCHASED);

                // payment 저장 검증
                Payment savedPayment = paymentRepository.findByMerchantUid(idempotencyKey).orElseThrow();
                assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.PAID);
                assertThat(savedPayment.getOrder()).isNotNull();
                assertThat(savedPayment.getOrder().getId()).isEqualTo(savedOrder.getId());
            }

            @Test
            void 동일_멱등키로_재요청하면_기존_결제결과를_반환한다() {
                // given
                String idempotencyKey = "checkout-idempotent";
                CheckoutRequest request = createCheckoutRequest(idempotencyKey);

                // when
                CheckoutResponse firstResponse = orderService.checkout(defaultUser.getId(), request);
                CheckoutResponse secondResponse = orderService.checkout(defaultUser.getId(), request);

                // then
                assertThat(secondResponse).isEqualTo(firstResponse);
                assertThat(orderRepository.count()).isEqualTo(1);
                assertThat(paymentRepository.count()).isEqualTo(1);
                assertThat(firstResponse.orderId()).isNotNull();
                assertThat(firstResponse.paymentId()).isNotNull();

                Payment savedPayment = paymentRepository.findByMerchantUid(idempotencyKey).orElseThrow();
                assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.PAID);
                assertThat(savedPayment.getOrder()).isNotNull();
                assertThat(savedPayment.getOrder().getId()).isEqualTo(firstResponse.orderId());
            }
        }

        @Nested
        class 유효하지_않은_요청이면 {

            @Test
            void 존재하지_않는_사용자면_예외가_발생한다() {
                // given
                String idempotencyKey = "checkout-no-user";
                CheckoutRequest request = createCheckoutRequest(idempotencyKey);

                // when // then
                assertThatThrownBy(() -> orderService.checkout("not-exist-user-id", request))
                        .isInstanceOf(CustomException.class)
                        .satisfies(throwable -> assertThat(((CustomException) throwable).getErrorCode())
                                .isEqualTo(ErrorCode.USER_NOT_FOUND));
                assertThat(orderRepository.count()).isZero();
                assertThat(paymentRepository.findByMerchantUid(idempotencyKey)).isEmpty();
            }

            @Test
            void 존재하지_않는_옵션이면_예외가_발생한다() {
                // given
                String idempotencyKey = "checkout-no-option";
                CheckoutRequest request = new CheckoutRequest(
                        "not-exist-option-id",
                        PaymentMethod.CARD,
                        150000L,
                        idempotencyKey
                );

                // when // then
                assertThatThrownBy(() -> orderService.checkout(defaultUser.getId(), request))
                        .isInstanceOf(CustomException.class)
                        .satisfies(throwable -> assertThat(((CustomException) throwable).getErrorCode())
                                .isEqualTo(ErrorCode.NOT_FOUND));
                assertThat(orderRepository.count()).isZero();
                assertThat(paymentRepository.findByMerchantUid(idempotencyKey)).isEmpty();
            }

            @Test
            void 결제금액이_옵션가격과_다르면_예외가_발생한다() {
                // given
                String idempotencyKey = "checkout-invalid-amount";
                CheckoutRequest request = new CheckoutRequest(
                        defaultProductOption.getId(),
                        PaymentMethod.CARD,
                        defaultProductOption.getCost() + 1L,
                        idempotencyKey
                );

                // when & then
                assertThatThrownBy(() -> orderService.checkout(defaultUser.getId(), request))
                        .isInstanceOf(CustomException.class)
                        .satisfies(throwable -> assertThat(((CustomException) throwable).getErrorCode())
                                .isEqualTo(ErrorCode.BAD_REQUEST));
                assertThat(orderRepository.count()).isZero();
                assertThat(paymentRepository.findByMerchantUid(idempotencyKey)).isEmpty();
            }

            @Test
            void 옵션이_판매중이_아니면_예외가_발생한다() {
                // given
                ProductOption notOnSaleOption = createProductOption(150000, 100, ProductStatus.END);
                String idempotencyKey = "checkout-not-on-sale";
                CheckoutRequest request = new CheckoutRequest(
                        notOnSaleOption.getId(),
                        PaymentMethod.CARD,
                        (long) notOnSaleOption.getCost(),
                        idempotencyKey
                );

                // when & then
                assertThatThrownBy(() -> orderService.checkout(defaultUser.getId(), request))
                        .isInstanceOf(CustomException.class)
                        .satisfies(throwable -> assertThat(((CustomException) throwable).getErrorCode())
                                .isEqualTo(ErrorCode.BAD_REQUEST));
                assertThat(orderRepository.count()).isZero();
                assertThat(paymentRepository.findByMerchantUid(idempotencyKey)).isEmpty();
            }

            @Test
            void 옵션의_재고가_없으면_예외가_발생한다() {
                // given
                ProductOption noStockOption = createProductOption(150000, 0, ProductStatus.ON_SALE);
                String idempotencyKey = "checkout-no-stock";
                CheckoutRequest request = new CheckoutRequest(
                        noStockOption.getId(),
                        PaymentMethod.CARD,
                        (long) noStockOption.getCost(),
                        idempotencyKey
                );

                // when & then
                assertThatThrownBy(() -> orderService.checkout(defaultUser.getId(), request))
                        .isInstanceOf(CustomException.class)
                        .satisfies(throwable -> assertThat(((CustomException) throwable).getErrorCode())
                                .isEqualTo(ErrorCode.BAD_REQUEST));
                assertThat(orderRepository.count()).isZero();
                assertThat(paymentRepository.findByMerchantUid(idempotencyKey)).isEmpty();
            }

            @Test
            void 같은_상품을_이미_주문했으면_예외가_발생한다() {
                // given
                orderRepository.save(Order.createPurchased(defaultUser, defaultProductOption, defaultProductOption.getCost()));
                String idempotencyKey = "checkout-already-ordered";
                CheckoutRequest request = createCheckoutRequest(idempotencyKey);

                // when & then
                assertThatThrownBy(() -> orderService.checkout(defaultUser.getId(), request))
                        .isInstanceOf(CustomException.class)
                        .satisfies(throwable -> assertThat(((CustomException) throwable).getErrorCode())
                                .isEqualTo(ErrorCode.BAD_REQUEST));
                assertThat(orderRepository.count()).isEqualTo(1);
                assertThat(paymentRepository.findByMerchantUid(idempotencyKey)).isEmpty();
            }
        }

        @Nested
        class 결제승인이_실패하면 {

            @Test
            void 주문은_생성되지_않고_실패_결제정보를_반환한다() {
                // given
                String idempotencyKey = "checkout-pg-fail";
                CheckoutRequest request = createCheckoutRequest(idempotencyKey.toUpperCase() + "-FAIL");

                // when
                CheckoutResponse response = orderService.checkout(defaultUser.getId(), request);

                // then
                assertThat(response.orderId()).isNull();
                assertThat(response.orderStatus()).isNull();
                assertThat(response.paymentId()).isNotNull();
                assertThat(response.paymentStatus()).isEqualTo(PaymentStatus.FAILED);
                assertThat(response.failureCode()).isEqualTo("FAKE_PG_DECLINED");
                assertThat(response.failureMessage()).isEqualTo("Fake PG 결제 실패");
                assertThat(orderRepository.count()).isZero();

                Payment savedPayment = paymentRepository.findByMerchantUid(request.idempotencyKey()).orElseThrow();
                assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.FAILED);
                assertThat(savedPayment.getOrder()).isNull();
                assertThat(savedPayment.getFailureCode()).isEqualTo("FAKE_PG_DECLINED");
            }
        }

        @Nested
        class 실패한_결제를_재시도하면 {

            @Test
            void 기존_실패_레코드를_재활용하여_결제승인을_재시도하고_성공하면_주문이_생성된다() {
                // given
                String idempotencyKey = "checkout-retry-success";

                Payment failedPayment = Payment.createRequested(
                        idempotencyKey,
                        PaymentMethod.CARD,
                        (long) defaultProductOption.getCost()
                );
                failedPayment.markFailed("TEMP_ERR", "임시 결제 실패");
                paymentRepository.save(failedPayment);

                CheckoutRequest request = createCheckoutRequest(idempotencyKey);

                // when
                CheckoutResponse response = orderService.checkout(defaultUser.getId(), request);

                // then
                assertThat(response.orderId()).isNotNull();
                assertThat(response.orderStatus()).isEqualTo(OrderStatus.PURCHASED);
                assertThat(response.paymentId()).isEqualTo(failedPayment.getId());
                assertThat(response.paymentStatus()).isEqualTo(PaymentStatus.PAID);
                assertThat(response.failureCode()).isNull();
                assertThat(response.failureMessage()).isNull();

                assertThat(paymentRepository.count()).isEqualTo(1);

                Payment savedPayment = paymentRepository.findByMerchantUid(idempotencyKey).orElseThrow();
                assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.PAID);
                assertThat(savedPayment.getOrder()).isNotNull();
                assertThat(savedPayment.getOrder().getId()).isEqualTo(response.orderId());
            }
        }
    }

    @Nested
    class 주문_내역을_조회할_때 {

        @Nested
        class 커서가_없을때 {

            @Test
            void 기본_사이즈로_조회하면_10개까지_조회된다() {
                // given
                LocalDateTime baseTime = LocalDateTime.of(2026, 3, 19, 10, 0, 0);
                createOrders(defaultUser, baseTime, 11);
                OrderHistoryRequest request = createOrderHistoryRequest(null, null, null);

                // when
                OrderHistoryResponse response = orderService.getOrderHistory(defaultUser.getId(), request);

                // then
                assertThat(response.content()).hasSize(10);
                assertThat(response.hasNext()).isTrue();
                assertThat(response.nextCursorOrderDate()).isEqualTo(response.content().get(9).orderedAt());
                assertThat(response.nextCursorOrderId()).isEqualTo(response.content().get(9).orderId());
                assertDescendingOrder(response.content());
            }

            @Test
            void 전체_건수가_size_이하면_hasNext_false_이고_next_cursor는_null이다() {
                // given
                LocalDateTime baseTime = LocalDateTime.of(2026, 3, 19, 10, 0, 0);
                createOrders(defaultUser, baseTime, 3);
                OrderHistoryRequest request = createOrderHistoryRequest(null, null, 3);

                // when
                OrderHistoryResponse response = orderService.getOrderHistory(defaultUser.getId(), request);

                // then
                assertThat(response.content()).hasSize(3);
                assertThat(response.hasNext()).isFalse();
                assertThat(response.nextCursorOrderDate()).isNull();
                assertThat(response.nextCursorOrderId()).isNull();
                assertDescendingOrder(response.content());
            }

            @Test
            void 전체_건수가_size를_초과하면_hasNext_true_이고_next_cursor는_마지막_항목이다() {
                // given
                LocalDateTime baseTime = LocalDateTime.of(2026, 3, 19, 10, 0, 0);
                createOrders(defaultUser, baseTime, 4);
                OrderHistoryRequest request = createOrderHistoryRequest(null, null, 3);

                // when
                OrderHistoryResponse response = orderService.getOrderHistory(defaultUser.getId(), request);

                // then
                assertThat(response.content()).hasSize(3);
                assertThat(response.hasNext()).isTrue();
                assertThat(response.nextCursorOrderDate()).isEqualTo(response.content().get(2).orderedAt());
                assertThat(response.nextCursorOrderId()).isEqualTo(response.content().get(2).orderId());
                assertDescendingOrder(response.content());
            }

            @Test
            void 주문이_없으면_빈_결과를_반환한다() {
                // given
                User userWithoutOrders = createUser("empty");
                OrderHistoryRequest request = createOrderHistoryRequest(null, null, 3);

                // when
                OrderHistoryResponse response = orderService.getOrderHistory(userWithoutOrders.getId(), request);

                // then
                assertThat(response.content()).isEmpty();
                assertThat(response.hasNext()).isFalse();
                assertThat(response.nextCursorOrderDate()).isNull();
                assertThat(response.nextCursorOrderId()).isNull();
            }
        }

        @Nested
        class 커서가_있을때 {

            @Test
            void 커서를_전달하면_다음_페이지_데이터만_조회된다() {
                // given
                LocalDateTime baseTime = LocalDateTime.of(2026, 3, 19, 10, 0, 0);
                List<Order> createdOrders = createOrders(defaultUser, baseTime, 5);
                OrderHistoryRequest firstRequest = createOrderHistoryRequest(null, null, 2);
                OrderHistoryResponse firstPage = orderService.getOrderHistory(defaultUser.getId(), firstRequest);

                OrderHistoryRequest nextRequest = createOrderHistoryRequest(
                        firstPage.nextCursorOrderDate(),
                        firstPage.nextCursorOrderId(),
                        2
                );

                // when
                OrderHistoryResponse nextPage = orderService.getOrderHistory(defaultUser.getId(), nextRequest);

                // then
                assertThat(firstPage.content()).hasSize(2);
                assertThat(firstPage.hasNext()).isTrue();
                assertThat(nextPage.content()).hasSize(2);
                assertThat(nextPage.content().get(0).orderId()).isEqualTo(createdOrders.get(2).getId());
                assertThat(nextPage.content().get(1).orderId()).isEqualTo(createdOrders.get(3).getId());
                assertDescendingOrder(nextPage.content());
            }
        }
    }

    private ProductOption createProductOption() {
        return createProductOption(150000, 100, ProductStatus.ON_SALE);
    }

    private ProductOption createProductOption(int cost, int stock, ProductStatus status) {
        Color color = colorRepository.save(Color.builder()
                .hexCode("#111111")
                .colorName("Black")
                .build());

        Product product = Product.createProduct(
                "테스트 신발",
                "테스트 설명",
                "NIKE",
                color,
                LocalDateTime.of(2026, 3, 1, 0, 0, 0),
                ProductCategory.SNEAKERS
        );

        product.addImage(ProductImage.builder()
                .imageUrl("https://test-image-url")
                .orders(0)
                .build());

        Product savedProduct = productRepository.save(product);

        ProductOption option = ProductOption.builder()
                .size(260)
                .cost(cost)
                .stock(stock)
                .status(status)
                .build();
        option.setProduct(savedProduct);

        return productOptionRepository.save(option);
    }

    private CheckoutRequest createCheckoutRequest(String idempotencyKey) {
        return new CheckoutRequest(
                defaultProductOption.getId(),
                PaymentMethod.CARD,
                (long) defaultProductOption.getCost(),
                idempotencyKey
        );
    }

    private User createUser(String prefix) {
        userSequence++;
        return userRepository.save(User.builder()
                .name(prefix + "-user")
                .email(prefix + userSequence + "@test.com")
                .password("password")
                .role(Role.USER)
                .build());
    }

    private List<Order> createOrders(User user, LocalDateTime baseTime, int count) {
        List<Order> savedOrders = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Order order = Order.builder()
                    .user(user)
                    .product(defaultProductOption.getProduct())
                    .productOption(defaultProductOption)
                    .cost(defaultProductOption.getCost())
                    .status(OrderStatus.PURCHASED)
                    .orderDate(baseTime.minusMinutes(i))
                    .build();
            savedOrders.add(orderRepository.save(order));
        }
        savedOrders.sort(
                Comparator.comparing(Order::getOrderDate).reversed()
                        .thenComparing(Order::getId, Comparator.reverseOrder())
        );
        return savedOrders;
    }

    private OrderHistoryRequest createOrderHistoryRequest(LocalDateTime cursorOrderDate, String cursorOrderId, Integer size) {
        return new OrderHistoryRequest(cursorOrderDate, cursorOrderId, size);
    }

    private void assertDescendingOrder(List<OrderHistoryResponse.OrderHistoryItem> content) {
        for (int i = 0; i < content.size() - 1; i++) {
            OrderHistoryResponse.OrderHistoryItem current = content.get(i);
            OrderHistoryResponse.OrderHistoryItem next = content.get(i + 1);
            assertThat(current.orderedAt()).isAfterOrEqualTo(next.orderedAt());
            if (current.orderedAt().isEqual(next.orderedAt())) {
                assertThat(current.orderId().compareTo(next.orderId())).isGreaterThanOrEqualTo(0);
            }
        }
    }
}
