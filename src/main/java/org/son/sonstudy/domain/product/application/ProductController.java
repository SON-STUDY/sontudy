package org.son.sonstudy.domain.product.application;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.api.code.SuccessCode;
import org.son.sonstudy.common.exception.CustomException;
import org.son.sonstudy.common.jwt.data.UserContext;
import org.son.sonstudy.domain.product.application.request.ProductRegistrationRequest;
import org.son.sonstudy.domain.product.application.request.ScheduledDropsRequest;
import org.son.sonstudy.domain.product.business.ProductService;
import org.son.sonstudy.domain.product.business.response.ProductDetailResponse;
import org.son.sonstudy.domain.product.business.response.ProductLiveResponse;
import org.son.sonstudy.domain.product.business.response.ProductResponse;
import org.son.sonstudy.domain.product.business.response.ScheduledDropsResponse;
import org.son.sonstudy.domain.product.dto.ProductSearchFilter;
import org.son.sonstudy.domain.product.model.ProductStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product", description = "상품 API")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다. ADMIN 또는 SELLER 권한이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "상품 등록 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "ADMIN 또는 SELLER 권한 없음")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<org.son.sonstudy.common.api.response.ApiResponse<Void>> register(
            @AuthenticationPrincipal UserContext userContext,
            @RequestBody @Valid ProductRegistrationRequest request
    ) {
        productService.register(userContext.userId(), request);

        return org.son.sonstudy.common.api.response.ApiResponse.success(SuccessCode.PRODUCT_REGISTERED);
    }

    @Operation(summary = "상품 상세 조회", description = "상품 ID로 특정 상품의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<org.son.sonstudy.common.api.response.ApiResponse<ProductDetailResponse>> getProductDetail(
            @Parameter(description = "상품 ID") @PathVariable String productId
    ) {
        ProductDetailResponse response = productService.findProductDetail(productId);

        return org.son.sonstudy.common.api.response.ApiResponse.success(SuccessCode.PRODUCT_OK, response);
    }

    @Operation(
            summary = "드랍 상태별 상품 조회",
            description = "dropStatus=SCHEDULED 파라미터로 예정된 드랍 상품 목록을 커서 기반 페이지네이션으로 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "지원하지 않는 dropStatus 값")
    })
    @GetMapping(params = "dropStatus")
    public ResponseEntity<org.son.sonstudy.common.api.response.ApiResponse<ScheduledDropsResponse>> getDropsByStatus(
            @AuthenticationPrincipal(errorOnInvalidType = false) UserContext userContext,
            @Parameter(description = "드랍 상태 (현재 SCHEDULED만 지원)") @RequestParam ProductStatus dropStatus,
            @ModelAttribute ScheduledDropsRequest request
    ) {
        if (dropStatus != ProductStatus.SCHEDULED) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        } // 다른 dropStatus 확장 시 분기 추가 하면 됨

        String userId = userContext != null ? userContext.userId() : null;
        ScheduledDropsRequest normalized = request.normalize(5);
        ScheduledDropsResponse response = productService.findScheduledDrops(userId, normalized);
        return org.son.sonstudy.common.api.response.ApiResponse.success(SuccessCode.PRODUCT_OK, response);
    }

    @Operation(summary = "라이브 드랍 상품 조회", description = "현재 라이브 중인 드랍 상품 목록을 페이지네이션으로 조회합니다. 기본 페이지 크기는 3입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/live")
    public ResponseEntity<org.son.sonstudy.common.api.response.ApiResponse<ProductLiveResponse>> getLiveDrops(
            @NullableUserId String userId,
            @PageableDefault(size = 3) Pageable pageable
    ) {
        ProductLiveResponse response = productService.findLiveDrops(userId, pageable);
        return org.son.sonstudy.common.api.response.ApiResponse.success(SuccessCode.PRODUCT_OK, response);
    }

    @Operation(
            summary = "상품 목록 조회",
            description = "브랜드(brand), 판매 상태(status) 필터와 페이지네이션을 적용하여 상품 목록을 조회합니다. 기본 정렬은 출시일 내림차순입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<org.son.sonstudy.common.api.response.ApiResponse<ProductResponse>> getProducts(
            ProductSearchFilter filter,
            @PageableDefault(size = 10, sort = "releasedAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        ProductResponse response = productService.findProducts(filter, pageable);

        return org.son.sonstudy.common.api.response.ApiResponse.success(SuccessCode.PRODUCT_OK, response);
    }
}
