package org.son.sonstudy.domain.product.application.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.son.sonstudy.domain.product.model.ProductCategory;

import java.time.LocalDateTime;
import java.util.List;


public record ProductRegistrationRequest(
        @NotBlank(message = "상품명은 필수입니다.")
        String name,

        @NotBlank(message = "상품 설명은 필수입니다.")
        String description,

        @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
        int cost,

        @NotBlank(message = "색상 이름은 필수입니다.")
        String colorName,

        @NotBlank(message = "색상 코드는 필수입니다.")
        String colorHexCode,

        String imageUrl,

        @NotBlank(message = "드랍 시간은 필수입니다.")
        LocalDateTime relasedAt,

        @NotNull(message = "카테고리는 필수입니다.")
        ProductCategory category,

        @NotEmpty(message = "최소 1개 이상의 옵션을 등록해야 합니다.")
        @Valid
        List<OptionRequest> options

) {
        public record OptionRequest(
                @Min(value = 150, message = "사이즈는 필수입니다.(100 이상)")
                int size,

                @Min(value = 1, message = "재고는 1개 이상이어야 합니다.")
                int stock
        ) {}
}
