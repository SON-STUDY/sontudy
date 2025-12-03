package org.son.sonstudy.domain.product.model.submodel;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "color")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hexCode;

    private String colorName;

    public Color(String hexCode, String colorName) {
        this.hexCode = hexCode;
        this.colorName = colorName;
    }
}
