package org.son.sonstudy.domain.product.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.son.sonstudy.common.exception.CustomException;
import org.son.sonstudy.common.util.SecurityUtils;
import org.son.sonstudy.domain.product.application.request.ProductRegistrationRequest;
import org.son.sonstudy.domain.product.application.request.ScheduledDropsRequest;
import org.son.sonstudy.domain.product.business.ProductService;
import org.son.sonstudy.domain.product.model.Product;
import org.son.sonstudy.domain.product.model.ProductCategory;
import org.son.sonstudy.domain.product.model.ProductOption;
import org.son.sonstudy.domain.product.model.submodel.Color;
import org.son.sonstudy.domain.product.model.submodel.ColorRepository;
import org.son.sonstudy.domain.product.model.submodel.ProductImage;
import org.son.sonstudy.domain.product.repository.ProductRepository;
import org.son.sonstudy.domain.user.model.Role;
import org.son.sonstudy.domain.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ProductServiceImplTest {

    @Autowired private ProductService productService;

    @Autowired private ProductRepository productRepository;
    @Autowired private ColorRepository colorRepository;

    @Nested
    class 상품을_등록할_때 {

        @Test
        void 유효한_요청이면_상품을_등록한다() {
            // given
            User seller = User.builder().role(Role.SELLER).build();

            ProductRegistrationRequest.OptionRequest option =
                    new ProductRegistrationRequest.OptionRequest(250, 150000, 100);

            ProductRegistrationRequest request = new ProductRegistrationRequest(
                    "테스트 신발",
                    "테스트 신발입니다.",
                    "NIKE",
                    "Black",
                    "#000000",
                    List.of("testimage.url"),
                    LocalDateTime.now(),
                    ProductCategory.SNEAKERS,
                    List.of(option));

            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(seller);

                // when
                productService.register(request);

                List<Product> products = productRepository.findAll();

                // then
                assertThat(products).hasSize(1);
                assertThat(products.get(0).getName()).isEqualTo("테스트 신발");
                assertThat(products.get(0).getOptions().get(0).getSize()).isEqualTo(250);

                assertThat(products.get(0).getColor().getColorName()).isEqualTo("Black");
            }
        }

        @Test
        void 유저_역할이_SELLER가_아닌_경우_예외가_발생한다() {
            User user = User.builder().role(Role.USER).build();

            ProductRegistrationRequest.OptionRequest option =
                    new ProductRegistrationRequest.OptionRequest(250, 150000, 100);

            ProductRegistrationRequest request = new ProductRegistrationRequest(
                    "테스트 신발",
                    "테스트 신발입니다.",
                    "NIKE",
                    "Black",
                    "#000000",
                    List.of("testimage.url"),
                    LocalDateTime.now(),
                    ProductCategory.SNEAKERS,
                    List.of(option));

            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(user);

                // when & then
                assertThatThrownBy(() -> productService.register(request))
                        .isInstanceOf(CustomException.class)
                        .hasMessage("판매자 권한이 필요합니다.");
            }
        }
    }

    @Nested
    class 예정된_드랍_상품을_조회할_때 {

        @Test
        void 정상_요청이면_커서_기반으로_응답한다() {
            Color color = colorRepository.save(new Color("#000000", "Black"));
            Product first = createProduct("A", color, LocalDateTime.now().plusDays(1));
            Product second = createProduct("B", color, LocalDateTime.now().plusDays(2));
            Product third = createProduct("C", color, LocalDateTime.now().plusDays(3));
            productRepository.saveAll(List.of(first, second, third));

            ScheduledDropsRequest request = new ScheduledDropsRequest(null, null, 2);

            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(null);

                var response = productService.findScheduledDrops(request);

                assertThat(response.content()).hasSize(2);
                assertThat(response.hasNext()).isTrue();
                assertThat(response.nextCursorId()).isEqualTo(response.content().get(1).id());
            }
        }

        @Test
        void size가_null이면_기본_크기로_응답한다() {
            Color color = colorRepository.save(new Color("#000000", "Black"));
            Product product = createProduct("테스트 신발", color, LocalDateTime.now().plusDays(1));
            productRepository.save(product);

            ScheduledDropsRequest request = new ScheduledDropsRequest(null, null, null);

            try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
                securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(null);

                var response = productService.findScheduledDrops(request);

                assertThat(response.content()).hasSize(1);
                assertThat(response.hasNext()).isFalse();
            }
        }

        @Test
        void size가_0이면_예외가_발생한다() {
            ScheduledDropsRequest request = new ScheduledDropsRequest(null, null, 0);

            assertThatThrownBy(() -> productService.findScheduledDrops(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }

    private Product createProduct(String name, Color color, LocalDateTime releasedAt) {
        Product product = Product.createProduct(
                name,
                "설명",
                "NIKE",
                color,
                releasedAt,
                ProductCategory.SNEAKERS
        );

        ProductOption option = ProductOption.builder()
                .size(250)
                .cost(150000)
                .stock(10)
                .build();
        product.addOption(option);

        ProductImage image = ProductImage.builder()
                .imageUrl("testimage.url")
                .orders(0)
                .build();
        product.addImage(image);

        return product;
    }
}
