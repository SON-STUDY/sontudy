package org.son.sonstudy.common.api.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // COMMON
    BAD_REQUEST("DC400_001", HttpStatus.BAD_REQUEST, "요청 실패"),
    NOT_FOUND("DC404_001", HttpStatus.NOT_FOUND, "데이터 없음"),

    // USER
    USER_NOT_FOUND("DC404_101", HttpStatus.NOT_FOUND, "존재하지 않는 유저"),
    EMAIL_DUPLICATE("DC409_101", HttpStatus.CONFLICT, "이미 존재하는 이메일"),

    // AUTH
    INVALID_TOKEN_FORMAT("DC400_201", HttpStatus.BAD_REQUEST, "유효하지 않은 JWT 토큰"),
    UNAUTHORIZED_TOKEN("DC401_201", HttpStatus.UNAUTHORIZED, "JWT 토큰 인증 실패"),
    EXPIRED_TOKEN("DC401_201", HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(final String code, final HttpStatus httpStatus, final String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
