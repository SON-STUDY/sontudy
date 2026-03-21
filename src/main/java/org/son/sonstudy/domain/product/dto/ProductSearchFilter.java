package org.son.sonstudy.domain.product.dto;

import org.son.sonstudy.domain.product.model.ProductStatus;

public record ProductSearchFilter(
        String brand,
        ProductStatus status
){

}
