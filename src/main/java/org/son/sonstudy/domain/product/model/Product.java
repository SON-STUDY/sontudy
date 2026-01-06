package org.son.sonstudy.domain.product.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.son.sonstudy.domain.product.model.submodel.Color;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private Color color;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime releasedAt;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductOption> options = new ArrayList<>();

    @Builder
    private Product(String name, String description, Color color,
                    String imageUrl, LocalDateTime releasedAt, ProductCategory category, ProductStatus status) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.imageUrl = imageUrl;
        this.releasedAt = releasedAt;
        this.category = category;
        this.status = status;
    }

    public static Product createProduct(String name, String description, Color color,
                                        String imageUrl, LocalDateTime releasedAt, ProductCategory category) {
        return Product.builder()
                .name(name)
                .description(description)
                .color(color)
                .imageUrl(imageUrl)
                .releasedAt(releasedAt)
                .category(category)
                .status(ProductStatus.PREPARE)
                .build();
    }

    public void addOption(ProductOption option) {
        this.options.add(option);
        option.setProduct(this);
    }
}