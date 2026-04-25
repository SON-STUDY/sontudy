package org.son.sonstudy.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.api.response.ApiResponse;
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
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e){
        log.warn("[CustomException] 에러 코드: {}, 메시지: {}", e.getErrorCode(), e.getMessage());
        return ApiResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String firstErrorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
        
        log.warn("[ValidationException] 유효성 검사 실패: {}", firstErrorMessage);
        return ApiResponse.fail(ErrorCode.BAD_REQUEST, firstErrorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("[HttpMessageNotReadableException] JSON 형식 오류: {}", e.getMessage());
        return ApiResponse.fail(ErrorCode.INVALID_JSON_FORMAT);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("[HttpRequestMethodNotSupportedException] 지원하지 않는 HTTP 메서드: {}", e.getMethod());
        return ApiResponse.fail(ErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("[MethodArgumentTypeMismatchException] 파라미터 타입 불일치: 파라미터={}, 기대값={}, 받은값={}",
                e.getName(), e.getRequiredType(), e.getValue());
        return ApiResponse.fail(ErrorCode.PARAMETER_TYPE_MISMATCH);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("[NoHandlerFoundException] 요청 경로를 찾을 수 없음: {} {}", e.getHttpMethod(), e.getRequestURL());
        return ApiResponse.fail(ErrorCode.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("[AccessDeniedException] 접근 권한 없음");
        return ApiResponse.fail(ErrorCode.FORBIDDEN_ACCESS);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("[AuthenticationException] 인증 실패");
        return ApiResponse.fail(ErrorCode.AUTHENTICATION_FAILED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleInternalServerException(Exception e) {
        log.error("[InternalServerError] 예상치 못한 서버 오류 발생", e);
        return ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
