package org.son.sonstudy.domain.product.repository;

import org.son.sonstudy.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
