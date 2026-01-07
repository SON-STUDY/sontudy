package org.son.sonstudy.domain.product.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.son.sonstudy.common.config.SecurityConfig;
import org.son.sonstudy.domain.product.application.ProductController;
import org.son.sonstudy.domain.product.application.request.ProductRegistrationRequest;
import org.son.sonstudy.domain.product.business.ProductService;
import org.son.sonstudy.domain.product.model.ProductCategory;
import org.son.sonstudy.domain.product.model.submodel.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureMockMvc(addFilters = false) // 스프링 시큐리티 필터 비활성화
public class ProductControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

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
                    "Black",
                    "#000000",
                    List.of("testimage.url"),
                    LocalDateTime.now(),
                    ProductCategory.SNEAKERS,
                    List.of(option));

            // when
            ResultActions perform = mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            perform.andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").exists())
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
                    "Black",
                    "#000000",
                    List.of("testimage.url"),
                    LocalDateTime.now(),
                    ProductCategory.SNEAKERS,
                    List.of(option));

            // when
            ResultActions perform = mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("DC401_001"))
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
                    "Black",
                    "#000000",
                    List.of("testimage.url"),
                    LocalDateTime.now(),
                    ProductCategory.SNEAKERS,
                    List.of(option));

            // when
            ResultActions perform = mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("DC401_001"))
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
                    "Black",
                    "#000000",
                    List.of("testimage.url"),
                    null,
                    ProductCategory.SNEAKERS,
                    List.of(option));

            // when
            ResultActions perform = mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("DC401_001"))
                    .andExpect(jsonPath("$.message").value("드랍 시간은 필수입니다."));
        }
    }

}
