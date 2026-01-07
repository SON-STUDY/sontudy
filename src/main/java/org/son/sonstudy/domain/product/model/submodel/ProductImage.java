package org.son.sonstudy.domain.product.model.submodel;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.son.sonstudy.domain.product.model.Product;

@Entity
@Table(name = "product_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {

    @Id @Tsid
    private String id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private String imageUrl;

    private int orders;

    @Builder
    public ProductImage(Product product, String imageUrl, int orders) {
        this.id = id;
        this.product = product;
        this.imageUrl = imageUrl;
        this.orders = orders;
    }
}
