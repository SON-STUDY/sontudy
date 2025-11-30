package org.son.sonstudy.domain.order.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.son.sonstudy.domain.delivery.model.Delivery;
import org.son.sonstudy.domain.product.model.Product;
import org.son.sonstudy.domain.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "order")
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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // 구매 확정 가격
    private int cost;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderDate;
}
