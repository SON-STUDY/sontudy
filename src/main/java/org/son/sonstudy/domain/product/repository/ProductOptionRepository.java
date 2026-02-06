package org.son.sonstudy.domain.product.repository;

import org.son.sonstudy.domain.product.model.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductOptionRepository extends JpaRepository<ProductOption, String> {
    List<ProductOption> findByProductId(String productId);

    @Query("SELECT po FROM ProductOption po WHERE po.product.id IN :productIds")
    List<ProductOption> findByProductIds(@Param("productIds") List<String> productIds);
}
