package org.son.sonstudy.common.api.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode {
    // COMMON
    OK("DC200_001", HttpStatus.OK, "요청 성공"),
    CREATED("DC201_002", HttpStatus.CREATED, "생성 성공");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    SuccessCode(final String code, final HttpStatus httpStatus, final String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
