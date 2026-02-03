package org.son.sonstudy.domain.product.repository;

import org.son.sonstudy.domain.product.model.ProductNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductNotificationRepository extends JpaRepository<ProductNotification, String> {
    @Query("""
            select pn.product.id
            from ProductNotification pn
            where pn.user.id = :userId
              and pn.product.id in :productIds
            """)
    List<String> findNotificationEnabledProductIds(
            @Param("userId") String userId,
            @Param("productIds") List<String> productIds
    );
}
