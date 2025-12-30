package org.son.sonstudy.domain.product.business;

import org.son.sonstudy.domain.product.application.request.ProductRegistrationRequest;

public interface ProductService {

    void registProduct(ProductRegistrationRequest request);
}
