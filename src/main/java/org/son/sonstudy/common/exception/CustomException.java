package org.son.sonstudy.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.son.sonstudy.common.api.code.ErrorCode;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
}
