package org.son.sonstudy.domain.product.repository;

import org.son.sonstudy.domain.product.model.Product;
import org.son.sonstudy.domain.product.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, String>, ProductRepositoryCustom {
    @Query("""
            select p
            from Product p
            where upper(p.brand) = upper(:brand)
            and p.status = :status
            """)
    Page<Product> findByBrandIgnoreCase(@Param("brand") String brand, @Param("status") ProductStatus status, Pageable pageable);
}
