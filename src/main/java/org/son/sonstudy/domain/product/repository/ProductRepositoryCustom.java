package org.son.sonstudy.domain.product.repository;

import org.son.sonstudy.domain.product.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepositoryCustom {
    List<Product> findScheduledDropsByCursor(LocalDateTime cursorReleasedAt, String cursorId, int size);

    Slice<Product> findLiveDrops(String userId, Pageable pageable);
}
