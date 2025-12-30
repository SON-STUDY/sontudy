package org.son.sonstudy.domain.product.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(name = "product_option")
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

}
