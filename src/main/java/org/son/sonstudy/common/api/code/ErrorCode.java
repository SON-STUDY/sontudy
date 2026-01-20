package org.son.sonstudy.common.api.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // COMMON
    BAD_REQUEST("DC400_001", HttpStatus.BAD_REQUEST, "요청 실패"),
    INVALID_JSON_FORMAT("DC400_002", HttpStatus.BAD_REQUEST, "잘못된 JSON 형식 요청."),
    PARAMETER_TYPE_MISMATCH("DC400_003", HttpStatus.BAD_REQUEST, "파라미터 불일치"),
    NOT_FOUND("DC404_001", HttpStatus.NOT_FOUND, "데이터 없음"),
    ENDPOINT_NOT_FOUND("DC404_002", HttpStatus.NOT_FOUND, "존재하지 않는 엔드포인트"),
    METHOD_NOT_ALLOWED("DC405_001", HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드"),

    // COMMON 500
    INTERNAL_SERVER_ERROR("DC500_001", HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류"),

    // USER
    USER_NOT_FOUND("DC404_101", HttpStatus.NOT_FOUND, "존재하지 않는 유저"),
    APPLICATION_NOT_FOUND("DC404_102", HttpStatus.NOT_FOUND, "신청서를 찾을 수 없습니다."),
    EMAIL_DUPLICATE("DC409_101", HttpStatus.CONFLICT, "이미 존재하는 이메일"),
    ALREADY_APPLIED("DC409_102", HttpStatus.CONFLICT, "이미 신청된 상태입니다."),

    // AUTH
    INVALID_TOKEN_FORMAT("DC400_201", HttpStatus.BAD_REQUEST, "유효하지 않은 JWT 토큰"),
    UNAUTHORIZED_TOKEN("DC401_201", HttpStatus.UNAUTHORIZED, "JWT 토큰 인증 실패"),
    AUTHENTICATION_FAILED("DC401_202", HttpStatus.UNAUTHORIZED, "인증에 실패하였습니다."),
    EXPIRED_TOKEN("DC401_203", HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰"),
    FORBIDDEN_ACCESS("DC403_201", HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_SELLER("DC403_202", HttpStatus.FORBIDDEN, "판매자 권한이 필요합니다."),
    SUPER_TOKEN_ACCESS_DENIED("DC403_203", HttpStatus.FORBIDDEN, "슈퍼 토큰 발급 권한이 없습니다."),

    // PRODUCT
    // 예외 기준은 임의로 잡아놓았습니다. 추후 정합시다.
    INVALID_PRODUCT_COST("DC400_301", HttpStatus.BAD_REQUEST, "상품 가격은 0원 이상이어야 합니다."),
    INVALID_PRODUCT_SIZE("DC400_302", HttpStatus.BAD_REQUEST, "유효하지 않은 상품 사이즈입니다."),
    INVALID_STOCK("DC400_303", HttpStatus.BAD_REQUEST, "재고량은 음수가 될 수 없습니다."),
    INVALID_IMAGE_SIZE("DC400_301", HttpStatus.BAD_REQUEST, "상품 이미지는 1-10장 사이여야 합니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(final String code, final HttpStatus httpStatus, final String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
