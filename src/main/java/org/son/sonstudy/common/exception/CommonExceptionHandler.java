package org.son.sonstudy.common.exception;

import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.api.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e){
        return ApiResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String firstErrorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();

        return ApiResponse.fail(ErrorCode.BAD_REQUEST, firstErrorMessage);
    }
}
