package org.son.sonstudy.domain.product.business.response;

import org.son.sonstudy.domain.product.model.Product;
import org.son.sonstudy.domain.product.model.ProductOption;
import org.son.sonstudy.domain.product.model.ProductStatus;
import org.son.sonstudy.domain.product.model.submodel.ProductImage;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record ProductLiveResponse(
        List<ProductLiveElement> content,
        int pageNumber,
        int pageSize,
        boolean hasNext
) {
    public static ProductLiveResponse from(
            Slice<Product> slice,
            Set<String> likedProductIds,
            Set<String> notificationEnabledProductIds,
            Map<String, List<ProductOption>> optionsByProductId
    ) {
        List<ProductLiveElement> content = slice.getContent().stream()
                .map(product -> ProductLiveElement.from(
                        product,
                        likedProductIds.contains(product.getId()),
                        notificationEnabledProductIds.contains(product.getId()),
                        optionsByProductId.getOrDefault(product.getId(), List.of())
                ))
                .toList();

        return new ProductLiveResponse(
                content,
                slice.getNumber(),
                slice.getSize(),
                slice.hasNext()
        );
    }

    public record ProductLiveElement(
            String id,
            String imageUrl,
            LocalDateTime releasedAt,
            String brand,
            String name,
            int price,
            int stock,
            ProductStatus status,
            boolean notificationEnabled,
            boolean liked
    ) {
        public static ProductLiveElement from(
                Product product,
                boolean liked,
                boolean notificationEnabled,
                List<ProductOption> options
        ) {
            String imageUrl = product.getImages().stream()
                    .min(Comparator.comparingInt(ProductImage::getOrders))
                    .map(ProductImage::getImageUrl)
                    .orElse(null);

            int price = options.stream()
                    .mapToInt(ProductOption::getCost)
                    .min()
                    .orElse(0);

            int stock = options.stream()
                    .mapToInt(ProductOption::getStock)
                    .min()
                    .orElse(0);

            return new ProductLiveElement(
                    product.getId(),
                    imageUrl,
                    product.getReleasedAt(),
                    product.getBrand(),
                    product.getName(),
                    price,
                    stock,
                    product.getStatus(),
                    notificationEnabled,
                    liked
            );
        }
    }
}
