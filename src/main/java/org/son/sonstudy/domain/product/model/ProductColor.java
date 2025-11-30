package org.son.sonstudy.domain.product.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_color")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ProductColor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hexCode;

    private String colorName;

    public ProductColor(String hexCode, String colorName) {
        this.hexCode = hexCode;
        this.colorName = colorName;
    }
}
