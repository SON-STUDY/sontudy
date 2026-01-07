package org.son.sonstudy.domain.product.business;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.exception.CustomException;
import org.son.sonstudy.common.util.SecurityUtils;
import org.son.sonstudy.domain.product.application.request.ProductRegistrationRequest;
import org.son.sonstudy.domain.product.model.Product;
import org.son.sonstudy.domain.product.model.ProductOption;
import org.son.sonstudy.domain.product.model.submodel.Color;
import org.son.sonstudy.domain.product.model.submodel.ColorRepository;
import org.son.sonstudy.domain.product.model.submodel.ProductImage;
import org.son.sonstudy.domain.product.repository.ProductRepository;
import org.son.sonstudy.domain.user.model.Role;
import org.son.sonstudy.domain.user.model.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;

    @Override
    @Transactional
    public void register(ProductRegistrationRequest request) {
        User currentUser = SecurityUtils.getCurrentUser();
        validateSeller(currentUser);
        validateImageSize(request.imageUrls().size());

        Color color = createOrGetColor(request.colorName(), request.colorHexCode());

        Product product = Product.createProduct(
                request.name(),
                request.description(),
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

    private void validateImageSize(int size) {
        if (size < 1  || size > 10) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_SIZE);
        }
    }

    private void validateSeller(User user) {
        if (user.getRole() != Role.SELLER) {
            throw new CustomException(ErrorCode.NOT_SELLER);
        }
    }

    // 일단 Color 부분은 임의로 구현했습니다.(상품이 주 구현 목적이니까)
    private Color createOrGetColor(String colorName, String colorHexCode) {
        return colorRepository.findByHexCode(colorHexCode)
                .orElseGet(() -> colorRepository.save(new Color(colorHexCode, colorName)));
    }
}
