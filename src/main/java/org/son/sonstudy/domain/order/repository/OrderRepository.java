package org.son.sonstudy.domain.order.repository;

import org.son.sonstudy.domain.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String>, OrderRepositoryCustom {
    boolean existsByUserIdAndProductId(String userId, String productId);

    @Query("SELECT o FROM Order o JOIN FETCH o.delivery WHERE o.id = :orderId")
    Optional<Order> findWithDeliveryById(@Param("orderId") String orderId);
}
