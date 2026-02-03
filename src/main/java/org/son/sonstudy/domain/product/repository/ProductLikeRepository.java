package org.son.sonstudy.domain.product.repository;

import org.son.sonstudy.domain.product.model.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductLikeRepository extends JpaRepository<ProductLike, String> {
    @Query("""
            select pl.product.id
            from ProductLike pl
            where pl.user.id = :userId
              and pl.product.id in :productIds
            """)
    List<String> findLikedProductIds(@Param("userId") String userId, @Param("productIds") List<String> productIds);
}
