package org.son.sonstudy.domain.order.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.son.sonstudy.domain.delivery.model.Delivery;
import org.son.sonstudy.domain.product.model.Product;
import org.son.sonstudy.domain.product.model.ProductOption;
import org.son.sonstudy.domain.user.model.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_orders_user_product", columnList = "user_id, product_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @Tsid
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_id")
    private ProductOption productOption;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // 구매 확정 가격
    private int cost;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderDate;

    @Builder
    private Order(User user, Product product, ProductOption productOption, Delivery delivery, int cost, OrderStatus status, LocalDateTime orderDate) {
        this.user = user;
        this.product = product;
        this.productOption = productOption;
        this.delivery = delivery;
        this.cost = cost;
        this.status = status;
        this.orderDate = orderDate;
    }

    public static Order createPurchased(User user, ProductOption productOption, int cost) {
        return Order.builder()
                .user(user)
                .product(productOption.getProduct())
                .productOption(productOption)
                .cost(cost)
                .status(OrderStatus.PURCHASED)
                .orderDate(LocalDateTime.now())
                .build();
    }
}
