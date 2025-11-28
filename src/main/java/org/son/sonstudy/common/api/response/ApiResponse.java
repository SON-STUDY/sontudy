package org.son.sonstudy.common.api.response;

import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.api.code.SuccessCode;
import org.springframework.http.ResponseEntity;

public record ApiResponse<T>(
        String code,
        String message,
        T data
) {
    public static <T> ResponseEntity<ApiResponse<T>> success(SuccessCode successCode, T data) {
        return ResponseEntity
                .status(successCode.getHttpStatus())
                .body(new ApiResponse<>(
                        successCode.getCode(),
                        successCode.getMessage(),
                        data
                ));
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(SuccessCode successCode) {
        return ResponseEntity
                .status(successCode.getHttpStatus())
                .body(new ApiResponse<>(
                        successCode.getCode(),
                        successCode.getMessage(),
                        null
                ));
    }

    public static <T> ResponseEntity<ApiResponse<T>> fail(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ApiResponse<>(
                        errorCode.getCode(),
                        errorCode.getMessage(),
                        null
                ));
    }
}
