package org.son.sonstudy.common.api.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode {
    // COMMON
    OK("DC200_001", HttpStatus.OK, "요청 성공"),
    CREATED("DC201_002", HttpStatus.CREATED, "생성 성공"),

    // USER
    LOGIN("DC200_101", HttpStatus.OK, "로그인 성공"),
    SELLER_APPLICATION_OK("DC200_102", HttpStatus.OK, "판매자 신청 성공"),
    SIGN_UP("DC201_101", HttpStatus.CREATED, "회원가입 성공"),
    SELLER_SUBMITTED("DC201_102", HttpStatus.CREATED, "판매자 등록 성공"),

    // PRODUCT
    PRODUCT_OK("DC200_401", HttpStatus.OK, "상품 조회 성공"),
    PRODUCT_REGISTERED("DC201_401", HttpStatus.CREATED, "상품 등록 성공");



    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    SuccessCode(final String code, final HttpStatus httpStatus, final String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
