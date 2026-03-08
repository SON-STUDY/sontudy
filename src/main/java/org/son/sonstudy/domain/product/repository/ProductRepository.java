package org.son.sonstudy.domain.product.repository;

import org.son.sonstudy.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository extends JpaRepository<Product, String>, ProductRepositoryCustom {
    Page<Product> findByBrandIgnoreCase(String brand, Pageable pageable);
}
