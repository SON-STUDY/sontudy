package org.son.sonstudy.domain.product.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.*;

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
        this.size = size;
        this.cost = cost;
        this.stock = stock;
        this.totalSales = 0L;
    }
}
