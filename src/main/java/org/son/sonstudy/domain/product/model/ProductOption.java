package org.son.sonstudy.domain.product.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.*;
import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.exception.CustomException;

@Entity
@Getter
@Table(name = "product_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {
    @Id
    @Tsid
    private String id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int size;

    @Column(nullable = false)
    private int cost;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private Long totalSales;

    @Builder
    public ProductOption(int size, int cost, int stock) {
        validateSize(size);
        validateCost(cost);
        validateStock(stock);

        this.size = size;
        this.cost = cost;
        this.stock = stock;
        this.totalSales = 0L;
    }

    private void validateSize(int size) {
        if (size < 100) {
            throw new CustomException(ErrorCode.INVALID_PRODUCT_SIZE);
        }
    }

    private void validateCost(int cost) {
        if (cost < 0) {
            throw new CustomException(ErrorCode.INVALID_PRODUCT_COST);
        }
    }

    private void validateStock(int stock) {
        if (stock < 0) {
            throw new CustomException(ErrorCode.INVALID_STOCK);
        }
    }
}
