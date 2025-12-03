package org.son.sonstudy.common.exception;

import org.son.sonstudy.common.api.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e){
        return ApiResponse.fail(e.getErrorCode());
    }
}
