package org.son.sonstudy.domain.product.business;

import org.son.sonstudy.domain.product.application.request.ProductRegistrationRequest;
import org.son.sonstudy.domain.product.business.response.ProductDetailResponse;
import org.son.sonstudy.domain.product.business.response.ProductResponse;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    void register(ProductRegistrationRequest request);

    ProductResponse findAllProducts(Pageable pageable);

    ProductDetailResponse findProductDetail(String productId);
}
