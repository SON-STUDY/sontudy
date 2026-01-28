package org.son.sonstudy.domain.product.repository;

import org.son.sonstudy.domain.product.model.Product;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepositoryCustom {
    List<Product> findScheduledDropsByCursor(LocalDateTime cursorReleasedAt, String cursorId, int size);
}
