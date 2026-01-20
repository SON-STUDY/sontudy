package org.son.sonstudy.domain.product.business.response;

import org.son.sonstudy.domain.product.model.Product;
import org.son.sonstudy.domain.product.model.ProductCategory;
import org.son.sonstudy.domain.product.model.ProductStatus;
import org.son.sonstudy.domain.product.model.submodel.ProductImage;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record ProductResponse(
        List<ProductElement> content,
        int currentPage,
        int totalPages,
        long totalElements,
        boolean isLast
) {
    public static ProductResponse from(Page<Product> products) {
        List<ProductElement> content = products.getContent().stream()
                .map(ProductElement::from)
                .toList();

        return new ProductResponse(
                content,
                products.getNumber(),
                products.getTotalPages(),
                products.getTotalElements(),
                products.isLast()
        );
    }

    public record ProductElement(
            String id,
            String name,
            String thumbnailUrl,
            ProductCategory category,
            ProductStatus status,
            LocalDateTime releasedAt
    ) {
        public static ProductElement from(Product product) {
            String thumbnailUrl = product.getImages().stream()
                    .min(Comparator.comparingInt(ProductImage::getOrders))
                    .map(ProductImage::getImageUrl)
                    .orElse(null);

            return new ProductElement(
                    product.getId(),
                    product.getName(),
                    thumbnailUrl,
                    product.getCategory(),
                    product.getStatus(),
                    product.getReleasedAt()
            );
        }
    }
}
