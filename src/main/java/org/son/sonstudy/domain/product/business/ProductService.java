package org.son.sonstudy.domain.product.business;

import org.son.sonstudy.domain.product.application.request.ProductRegistrationRequest;
import org.son.sonstudy.domain.product.application.request.ScheduledDropsRequest;
import org.son.sonstudy.domain.product.business.response.ProductDetailResponse;
import org.son.sonstudy.domain.product.business.response.ProductLiveResponse;
import org.son.sonstudy.domain.product.business.response.ProductResponse;
import org.son.sonstudy.domain.product.business.response.ScheduledDropsResponse;
import org.son.sonstudy.domain.product.dto.ProductSearchFilter;
import org.son.sonstudy.domain.product.repository.ProductRepository;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    void register(String userId, ProductRegistrationRequest request);

    ProductResponse findProducts(ProductSearchFilter filter, Pageable pageable);

    ProductDetailResponse findProductDetail(String productId);

    ScheduledDropsResponse findScheduledDrops(String userId, ScheduledDropsRequest request);

    ProductLiveResponse findLiveDrops(String userId, Pageable pageable);
}
