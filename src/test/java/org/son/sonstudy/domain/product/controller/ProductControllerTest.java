package org.son.sonstudy.domain.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.son.sonstudy.common.config.SecurityConfig;
import org.son.sonstudy.common.exception.CommonExceptionHandler;
import org.son.sonstudy.common.jwt.filter.JwtAuthenticationFilter;
import org.son.sonstudy.domain.product.application.ProductController;
import org.son.sonstudy.domain.product.application.request.ProductRegistrationRequest;
import org.son.sonstudy.domain.product.application.request.ScheduledDropsRequest;
import org.son.sonstudy.domain.product.business.ProductService;
import org.son.sonstudy.domain.product.business.response.ScheduledDropsResponse;
import org.son.sonstudy.domain.product.model.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.son.sonstudy.common.jwt.data.UserContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)})
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProductController productController;

    @MockitoBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setCustomArgumentResolvers(new TestAuthenticationPrincipalArgumentResolver())
                .setControllerAdvice(new CommonExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Nested
    class 상품을_등록할_때 { // 예외 테스트는 너무 많고 반복 코드가 많아서 애노테이션별로 하나씩만 진행함

        @Test
        void 유효한_요청이면_상품을_등록한다() throws Exception {
            // given
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

            // when
            ResultActions perform = mockMvc.perform(post("/api/products")
                    .principal(sellerAuthentication())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            perform.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value("DC201_401"))
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        void 상품_이름이_빈칸이면_예외가_발생한다() throws Exception {
            // given
            ProductRegistrationRequest.OptionRequest option =
                    new ProductRegistrationRequest.OptionRequest(250, 150000, 100);

            ProductRegistrationRequest request = new ProductRegistrationRequest(
                    null,
                    "테스트 신발입니다.",
                    "NIKE",
                    "Black",
                    "#000000",
                    List.of("testimage.url"),
                    LocalDateTime.now(),
                    ProductCategory.SNEAKERS,
                    List.of(option));

            // when
            ResultActions perform = mockMvc.perform(post("/api/products")
                    .principal(sellerAuthentication())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("DC400_001"))
                    .andExpect(jsonPath("$.message").value("상품명은 필수입니다."));
        }

        @ParameterizedTest
        @ValueSource(ints = {-100, -5, -50000})
        void 가격이_0원_미만이면_예외가_발생한다(int cost) throws Exception {
            // given
            ProductRegistrationRequest.OptionRequest option =
                    new ProductRegistrationRequest.OptionRequest(250, cost, 100);

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

            // when
            ResultActions perform = mockMvc.perform(post("/api/products")
                    .principal(sellerAuthentication())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("DC400_001"))
                    .andExpect(jsonPath("$.message").value("가격에 음수를 입력할 수 없습니다."));
        }

        @Test
        void 드랍_시간이_빈칸이면_예외가_발생한다() throws Exception {
            // given
            ProductRegistrationRequest.OptionRequest option =
                    new ProductRegistrationRequest.OptionRequest(250, 150000, 100);

            ProductRegistrationRequest request = new ProductRegistrationRequest(
                    "테스트 신발",
                    "테스트 신발입니다.",
                    "NIKE",
                    "Black",
                    "#000000",
                    List.of("testimage.url"),
                    null,
                    ProductCategory.SNEAKERS,
                    List.of(option));

            // when
            ResultActions perform = mockMvc.perform(post("/api/products")
                    .principal(sellerAuthentication())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("DC400_001"))
                    .andExpect(jsonPath("$.message").value("드랍 시간은 필수입니다."));
        }
    }

    @Nested
    class 예정된_드랍_상품을_조회할_때 {

        @Test
        void 정상_요청이면_예정된_드랍_목록을_응답한다() throws Exception {
            // given
            LocalDateTime releasedAt = LocalDateTime.of(2026, 2, 1, 10, 0);
            ScheduledDropsResponse.ScheduledDropElement element =
                    new ScheduledDropsResponse.ScheduledDropElement(
                            "product-1",
                            "https://cdn.example.com/products/1/main.jpg",
                            releasedAt,
                            "NIKE",
                            "Air Max 1",
                            179000,
                            42,
                            true,
                            false
                    );
            ScheduledDropsResponse response = new ScheduledDropsResponse(
                    List.of(element),
                    "product-1",
                    releasedAt,
                    false
            );
            given(productService.findScheduledDrops(isNull(), any(ScheduledDropsRequest.class))).willReturn(response);

            // when (익명 사용자 - principal 없음)
            ResultActions perform = mockMvc.perform(get("/api/products")
                    .param("dropStatus", "scheduled")
                    .param("size", "5"));

            // then
            perform.andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data.content[0].id").value("product-1"))
                    .andExpect(jsonPath("$.data.content[0].brand").value("NIKE"))
                    .andExpect(jsonPath("$.data.content[0].name").value("Air Max 1"))
                    .andExpect(jsonPath("$.data.nextCursorId").value("product-1"));
        }

        @Test
        void 커서_날짜_형식이_잘못되면_예외를_응답한다() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(get("/api/products")
                    .param("dropStatus", "scheduled")
                    .param("cursorReleasedAt", "not-a-date")
                    .param("size", "5"));

            // then
            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("DC400_001"))
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        void dropStatus가_잘못되면_예외를_응답한다() throws Exception {
            // when
            ResultActions perform = mockMvc.perform(get("/api/products")
                    .param("dropStatus", "invalid-status")
                    .param("size", "5"));

            // then
            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("DC400_003"))
                    .andExpect(jsonPath("$.message").exists());
        }
    }

    private UsernamePasswordAuthenticationToken sellerAuthentication() {
        UserContext userContext = new UserContext(
                "user-123",
                "seller@test.com",
                org.son.sonstudy.domain.user.model.Role.SELLER
        );
        return new UsernamePasswordAuthenticationToken(
                userContext,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_SELLER"))
        );
    }

    private static class TestAuthenticationPrincipalArgumentResolver
            implements org.springframework.web.method.support.HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(org.springframework.core.MethodParameter parameter) {
            return parameter.hasParameterAnnotation(
                    org.springframework.security.core.annotation.AuthenticationPrincipal.class
            );
        }

        @Override
        public Object resolveArgument(
                org.springframework.core.MethodParameter parameter,
                org.springframework.web.method.support.ModelAndViewContainer mavContainer,
                org.springframework.web.context.request.NativeWebRequest webRequest,
                org.springframework.web.bind.support.WebDataBinderFactory binderFactory
        ) {
            org.springframework.security.core.Authentication authentication =
                    org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null && webRequest.getUserPrincipal() instanceof org.springframework.security.core.Authentication auth) {
                authentication = auth;
            }

            if (authentication == null) {
                org.springframework.security.core.annotation.AuthenticationPrincipal annotation =
                        parameter.getParameterAnnotation(
                                org.springframework.security.core.annotation.AuthenticationPrincipal.class
                        );
                if (annotation != null && !annotation.errorOnInvalidType()) {
                    return null;
                }
                return null;
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof UserContext) {
                return principal;
            }
            return null;
        }
    }

}
