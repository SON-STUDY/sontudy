package org.son.sonstudy.domain.product.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.son.sonstudy.domain.product.model.submodel.Color;
import org.son.sonstudy.domain.product.model.submodel.ProductImage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id
    @Tsid
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private Color color;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime releasedAt;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductOption> options = new ArrayList<>();

    @Builder
    private Product(String name, String description, String brand, Color color,
                    LocalDateTime releasedAt, ProductCategory category, ProductStatus status) {
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.color = color;
        this.releasedAt = releasedAt;
        this.category = category;
        this.status = status;
    }

    public static Product createProduct(String name, String description, String brand, Color color,
                                        LocalDateTime releasedAt, ProductCategory category) {
        return Product.builder()
                .name(name)
                .description(description)
                .brand(brand)
                .color(color)
                .releasedAt(releasedAt)
                .category(category)
                .status(ProductStatus.PREPARE)
                .build();
    }

    public void addOption(ProductOption option) {
        this.options.add(option);
        option.setProduct(this);
    }

    public void addImage(ProductImage image) {
        this.images.add(image);
        image.setProduct(this);
    }
}
