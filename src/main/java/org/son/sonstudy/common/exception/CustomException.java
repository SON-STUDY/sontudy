package org.son.sonstudy.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.ErrorCode;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 부모 생성자에 메시지 넘겨 디버깅 편의 up
        this.errorCode = errorCode;
    }
}
