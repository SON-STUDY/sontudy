package org.son.sonstudy.domain.order.repository;

import org.son.sonstudy.domain.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
    boolean existsByUserIdAndProductId(String userId, String productId);
}
