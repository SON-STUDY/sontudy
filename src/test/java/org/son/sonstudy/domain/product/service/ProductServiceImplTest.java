package org.son.sonstudy.domain.product.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.son.sonstudy.common.exception.CustomException;
import org.son.sonstudy.common.util.SecurityUtils;
import org.son.sonstudy.domain.product.application.request.ProductRegistrationRequest;
import org.son.sonstudy.domain.product.business.ProductService;
import org.son.sonstudy.domain.product.model.Product;
import org.son.sonstudy.domain.product.model.ProductCategory;
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
}
