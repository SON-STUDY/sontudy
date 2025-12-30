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

    private int size;
    private int stock;
    private int totalSales;

    @Builder
    public ProductOption(String id, int size, int stock, int totalSales) {
        this.id = id;
        this.size = size;
        this.stock = stock;
        this.totalSales = totalSales;
    }
}
