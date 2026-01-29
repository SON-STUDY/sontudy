package org.son.sonstudy.domain.product.business;

import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.exception.CustomException;
import org.son.sonstudy.common.util.SecurityUtils;
import org.son.sonstudy.domain.product.application.request.ProductRegistrationRequest;
import org.son.sonstudy.domain.product.application.request.ScheduledDropsRequest;
import org.son.sonstudy.domain.product.business.response.ProductDetailResponse;
import org.son.sonstudy.domain.product.business.response.ProductResponse;
import org.son.sonstudy.domain.product.business.response.ScheduledDropsResponse;
import org.son.sonstudy.domain.product.model.Product;
import org.son.sonstudy.domain.product.model.ProductOption;
import org.son.sonstudy.domain.product.model.submodel.Color;
import org.son.sonstudy.domain.product.model.submodel.ColorRepository;
import org.son.sonstudy.domain.product.model.submodel.ProductImage;
import org.son.sonstudy.domain.product.repository.ProductLikeRepository;
import org.son.sonstudy.domain.product.repository.ProductNotificationRepository;
import org.son.sonstudy.domain.product.repository.ProductRepository;
import org.son.sonstudy.domain.user.model.Role;
import org.son.sonstudy.domain.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;
    private final ProductLikeRepository productLikeRepository;
    private final ProductNotificationRepository productNotificationRepository;

    @Override
    @Transactional
    public void register(ProductRegistrationRequest request) {
        User user = SecurityUtils.getCurrentUser();

        validateUserRole(user);
        validateImageSize(request.imageUrls().size());

        Color color = createOrGetColor(request.colorName(), request.colorHexCode());

        Product product = Product.createProduct(
                request.name(),
                request.description(),
                request.brand(),
                color,
                request.releasedAt(),
                request.category()
        );

        for (var optionDto : request.options()) {
            ProductOption option = ProductOption.builder()
                    .size(optionDto.size())
                    .cost(optionDto.cost())
                    .stock(optionDto.stock())
                    .build();

            product.addOption(option);
        }

        for (int i = 0; i < request.imageUrls().size(); i++) {
            ProductImage image = ProductImage.builder()
                    .imageUrl(request.imageUrls().get(i))
                    .orders(i)
                    .build();

            product.addImage(image);
        }

        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);

        return ProductResponse.from(products);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailResponse findProductDetail(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        return ProductDetailResponse.from(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduledDropsResponse findScheduledDrops(ScheduledDropsRequest request) {
        int size = request.size() != null ? request.size() : 5;
        List<Product> products = productRepository.findScheduledDropsByCursor(
                request.cursorReleasedAt(),
                request.cursorId(),
                size);

        boolean hasNext = products.size() > size;
        List<Product> content = hasNext ? products.subList(0, size) : products;
        Slice<Product> slice = new SliceImpl<>(content, Pageable.ofSize(size), hasNext);

        User user = SecurityUtils.getCurrentUser();
        Set<String> likedProductIds = new HashSet<>();
        Set<String> notificationEnabledProductIds = new HashSet<>();
        if (user != null && !content.isEmpty()) {
            List<String> productIds = content.stream()
                    .map(Product::getId)
                    .toList();
            likedProductIds.addAll(
                    productLikeRepository.findLikedProductIds(user.getId(), productIds)
            );
            notificationEnabledProductIds.addAll(
                    productNotificationRepository.findNotificationEnabledProductIds(user.getId(), productIds)
            );
        }

        return ScheduledDropsResponse.from(
                slice,
                likedProductIds,
                notificationEnabledProductIds
        );
    }

    private void validateUserRole(User user) {
        if (user.getRole().equals(Role.USER)) {
            throw new CustomException(ErrorCode.NOT_SELLER);
        }
    }

    private void validateImageSize(int size) {
        if (size < 1  || size > 10) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_SIZE);
        }
    }

    private Color createOrGetColor(String colorName, String colorHexCode) {
        return colorRepository.findByHexCode(colorHexCode)
                .orElseGet(() -> colorRepository.save(new Color(colorHexCode, colorName)));
    }
}
