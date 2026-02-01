package org.son.sonstudy.domain.product.business.response;

import org.son.sonstudy.domain.product.model.Product;
import org.son.sonstudy.domain.product.model.ProductOption;
import org.son.sonstudy.domain.product.model.submodel.ProductImage;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public record ScheduledDropsResponse(
        List<ScheduledDropElement> content,
        String nextCursorId,
        LocalDateTime nextCursorReleasedAt,
        boolean hasNext
) {
    public static ScheduledDropsResponse from(
            Slice<Product> products,
            Set<String> likedProductIds,
            Set<String> notificationEnabledProductIds
    ) {
        List<ScheduledDropElement> content = products.getContent().stream()
                .map(product -> ScheduledDropElement.from(
                        product,
                        likedProductIds.contains(product.getId()),
                        notificationEnabledProductIds.contains(product.getId())
                ))
                .toList();

        if (content.isEmpty()) {
            return new ScheduledDropsResponse(content, null, null, false);
        }

        ScheduledDropElement last = content.get(content.size() - 1);
        return new ScheduledDropsResponse(
                content,
                last.id(),
                last.releasedAt(),
                products.hasNext()
        );
    }

    public record ScheduledDropElement(
            String id,
            String imageUrl,
            LocalDateTime releasedAt,
            String brand,
            String name,
            int price,
            int stock,
            boolean notificationEnabled,
            boolean liked
    ) {
        public static ScheduledDropElement from(
                Product product,
                boolean liked,
                boolean notificationEnabled
        ) {
            String imageUrl = product.getImages().stream()
                    .min(Comparator.comparingInt(ProductImage::getOrders))
                    .map(ProductImage::getImageUrl)
                    .orElse(null);

            int price = product.getOptions().stream()
                    .mapToInt(ProductOption::getCost)
                    .min()
                    .orElse(0);

            int stock = product.getOptions().stream()
                    .mapToInt(ProductOption::getStock)
                    .sum();

            return new ScheduledDropElement(
                    product.getId(),
                    imageUrl,
                    product.getReleasedAt(),
                    product.getBrand(),
                    product.getName(),
                    price,
                    stock,
                    notificationEnabled,
                    liked
            );
        }
    }
}
