package org.son.sonstudy.common.api.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // COMMON
    BAD_REQUEST("DC401_001",HttpStatus.BAD_REQUEST, "요청 실패"),
    NOT_FOUND("DC404_001", HttpStatus.NOT_FOUND, "데이터 없음"),

    // USER
    USER_NOT_FOUND("DC404_101", HttpStatus.NOT_FOUND, "존재하지 않는 유저");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(final String code, final HttpStatus httpStatus, final String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
