package org.son.sonstudy.domain.product.business.response;

import org.son.sonstudy.domain.product.model.Product;
import org.son.sonstudy.domain.product.model.ProductCategory;
import org.son.sonstudy.domain.product.model.ProductOption;
import org.son.sonstudy.domain.product.model.ProductStatus;
import org.son.sonstudy.domain.product.model.submodel.ProductImage;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record ProductDetailResponse(
        String id,
        String name,
        String description,
        String colorName,
        String colorHexCode,
        List<String> imageUrls,
        ProductCategory category,
        ProductStatus status,
        LocalDateTime releasedAt,
        List<OptionResponse> options
) {
    public static ProductDetailResponse from(Product product) {
        List<String> images = product.getImages().stream()
                .sorted(Comparator.comparingInt(ProductImage::getOrders))
                .map(ProductImage::getImageUrl)
                .toList();

        List<OptionResponse> options = product.getOptions().stream()
                .map(OptionResponse::from)
                .toList();

        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getColor().getColorName(),
                product.getColor().getHexCode(),
                images,
                product.getCategory(),
                product.getStatus(),
                product.getReleasedAt(),
                options
        );
    }

    public record OptionResponse(
            String id,
            int size,
            int cost,
            int stock
    ) {
        public static OptionResponse from(ProductOption option) {
            return new OptionResponse(
                    option.getId(),
                    option.getSize(),
                    option.getCost(),
                    option.getStock()
            );
        }
    }
}
