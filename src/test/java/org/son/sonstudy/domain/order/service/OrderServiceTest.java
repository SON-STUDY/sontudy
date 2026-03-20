package org.son.sonstudy.domain.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.son.sonstudy.domain.order.application.request.OrderHistoryRequest;
import org.son.sonstudy.domain.order.business.OrderService;
import org.son.sonstudy.domain.order.business.response.OrderHistoryResponse;
import org.son.sonstudy.domain.order.model.Order;
import org.son.sonstudy.domain.order.model.OrderStatus;
import org.son.sonstudy.domain.order.repository.OrderRepository;
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

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class OrderServiceTest {

    @Autowired private OrderService orderService;

    @Autowired private UserRepository userRepository;

    @Autowired private OrderRepository orderRepository;

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
                .cost(150000)
                .stock(100)
                .status(ProductStatus.ON_SALE)
                .build();
        option.setProduct(savedProduct);

        return productOptionRepository.save(option);
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
