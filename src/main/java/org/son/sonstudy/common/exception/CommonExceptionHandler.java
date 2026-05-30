package org.son.sonstudy.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.api.response.ApiResponse;
import org.son.sonstudy.common.logging.ExceptionLog;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.slf4j.MDC;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e){
        log.warn("[CustomException]", ExceptionLog.of("CustomException").field("errorCode", e.getErrorCode().name()).field("message", e.getMessage()).build());
        return ApiResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String firstErrorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();

        log.warn("[ValidationException]", ExceptionLog.of("ValidationException").field("message", firstErrorMessage).build());
        return ApiResponse.fail(ErrorCode.BAD_REQUEST, firstErrorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("[HttpMessageNotReadableException]", ExceptionLog.of("HttpMessageNotReadableException").build());
        return ApiResponse.fail(ErrorCode.INVALID_JSON_FORMAT);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("[HttpRequestMethodNotSupportedException]", ExceptionLog.of("HttpRequestMethodNotSupportedException").field("method", e.getMethod()).build());
        return ApiResponse.fail(ErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("[MethodArgumentTypeMismatchException]", ExceptionLog.of("MethodArgumentTypeMismatchException").field("param", e.getName()).field("expectedType", e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown").build());
        return ApiResponse.fail(ErrorCode.PARAMETER_TYPE_MISMATCH);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("[NoHandlerFoundException]", ExceptionLog.of("NoHandlerFoundException").field("method", e.getHttpMethod()).field("url", e.getRequestURL()).build());
        return ApiResponse.fail(ErrorCode.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("[AccessDeniedException]", ExceptionLog.of("AccessDeniedException").build());
        return ApiResponse.fail(ErrorCode.FORBIDDEN_ACCESS);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("[AuthenticationException]", ExceptionLog.of("AuthenticationException")
                .field("ip", MDC.get("ip"))
                .build());
        return ApiResponse.fail(ErrorCode.AUTHENTICATION_FAILED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleInternalServerException(Exception e) {
        log.error("[InternalServerError]", ExceptionLog.of("InternalServerError").buildWithThrowable(e));
        return ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
