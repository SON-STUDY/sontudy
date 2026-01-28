package org.son.sonstudy.domain.product.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.SuccessCode;
import org.son.sonstudy.common.api.response.ApiResponse;
import org.son.sonstudy.domain.product.application.request.ProductRegistrationRequest;
import org.son.sonstudy.domain.product.application.request.ScheduledDropsRequest;
import org.son.sonstudy.domain.product.business.ProductService;
import org.son.sonstudy.domain.product.business.response.ProductDetailResponse;
import org.son.sonstudy.domain.product.business.response.ProductResponse;
import org.son.sonstudy.domain.product.business.response.ScheduledDropsResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid ProductRegistrationRequest request) {
        productService.register(request);

        return ApiResponse.success(SuccessCode.PRODUCT_REGISTERED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProductResponse>> getAllProducts(
            @PageableDefault(size = 10, sort = "releasedAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        ProductResponse response = productService.findAllProducts(pageable);

        return ApiResponse.success(SuccessCode.PRODUCT_OK, response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductDetail(
            @PathVariable String productId
    ) {
        ProductDetailResponse response = productService.findProductDetail(productId);

        return ApiResponse.success(SuccessCode.PRODUCT_OK, response);
    }

    @GetMapping(params = "dropStatus=scheduled")
    public ResponseEntity<ApiResponse<ScheduledDropsResponse>> getScheduledDrops(
            @ModelAttribute ScheduledDropsRequest request
    ) {
        ScheduledDropsRequest normalized = request.normalize(5);
        ScheduledDropsResponse response = productService.findScheduledDrops(normalized);
        return ApiResponse.success(SuccessCode.PRODUCT_OK, response);
    }
}
