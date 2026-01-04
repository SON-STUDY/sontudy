package org.son.sonstudy.domain.product.model.submodel;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "color")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hexCode;

    private String colorName;

    @Builder
    public Color(String hexCode, String colorName) {
        this.hexCode = hexCode;
        this.colorName = colorName;
    }
}