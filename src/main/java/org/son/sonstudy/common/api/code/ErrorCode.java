package org.son.sonstudy.common.api.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // COMMON
    BAD_REQUEST("DC401_001",HttpStatus.BAD_REQUEST, "요청 실패"),
    NOT_FOUND("DC404_001", HttpStatus.NOT_FOUND, "데이터 없음"),

    // USER
    USER_NOT_FOUND("DC404_101", HttpStatus.NOT_FOUND, "존재하지 않는 유저"),
    EMAIL_DUPLICATE("DC409_101", HttpStatus.CONFLICT, "이미 존재하는 이메일"),

    // AUTH
    FORBIDDEN_ACCESS("DC403_201", HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_SELLER("DC403_202", HttpStatus.FORBIDDEN, "판매자 권한이 필요합니다."),

    // PRODUCT
    // 예외 기준은 임의로 잡아놓았습니다. 추후 정합시다.
    INVALID_PRODUCT_COST("DC400_301", HttpStatus.BAD_REQUEST, "상품 가격은 0원 이상이어야 합니다."),
    INVALID_PRODUCT_SIZE("DC400_302", HttpStatus.BAD_REQUEST, "유효하지 않은 상품 사이즈입니다."),
    INVALID_STOCK("DC400_303", HttpStatus.BAD_REQUEST, "재고량은 음수가 될 수 없습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(final String code, final HttpStatus httpStatus, final String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
