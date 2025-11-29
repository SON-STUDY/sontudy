package org.son.sonstudy.domain.product.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
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
    private int cost;

    @Column(nullable = false)
    private int size;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private Long totalSales;

    @Column(nullable = false)
    private LocalDateTime releasedAt;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;
}
