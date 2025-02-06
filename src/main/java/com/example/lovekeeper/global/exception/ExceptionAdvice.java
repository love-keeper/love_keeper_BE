package com.example.lovekeeper.global.exception;

import java.nio.file.AccessDeniedException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.lovekeeper.global.common.BaseResponse;
import com.example.lovekeeper.global.exception.code.BaseCode;
import com.example.lovekeeper.global.exception.code.GlobalErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
@RequiredArgsConstructor
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

	/**
	 * 1) 직접 정의한 비즈니스/도메인 예외 처리
	 */
	@ExceptionHandler(value = BaseException.class)
	public ResponseEntity<BaseResponse<String>> handleRestApiException(BaseException e) {
		BaseCode errorCode = e.getErrorCode();
		log.error("[handleRestApiException] {}", e.getMessage(), e);
		return handleExceptionInternal(errorCode);
	}

	/**
	 * 2) 예상치 못한 모든 서버 예외 처리
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<BaseResponse<String>> handleException(Exception e) {
		log.error("[handleException] {}", e.getMessage(), e);

		// _INTERNAL_SERVER_ERROR 코드와 함께, 실제 발생한 메시지를 추가
		return handleExceptionInternalFalse(GlobalErrorStatus._INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
	}

	/**
	 * 3) ConstraintViolationException (파라미터 검증, 메서드 검증 실패)
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<BaseResponse<String>> handleConstraintViolationException(ConstraintViolationException e) {
		log.error("[handleConstraintViolationException] {}", e.getMessage(), e);

		return handleExceptionInternal(GlobalErrorStatus._VALIDATION_ERROR.getCode());
	}

	/**
	 * 4) MethodArgumentTypeMismatchException
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<BaseResponse<String>> handleMethodArgumentTypeMismatch(
		MethodArgumentTypeMismatchException e) {
		log.error("[handleMethodArgumentTypeMismatch] {}", e.getMessage(), e);
		return handleExceptionInternal(GlobalErrorStatus._METHOD_ARGUMENT_ERROR.getCode());
	}

	/**
	 * 5) MethodArgumentNotValidException
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		MethodArgumentNotValidException e,
		HttpHeaders headers,
		HttpStatusCode statusCode,
		WebRequest request
	) {
		Map<String, String> errors = new LinkedHashMap<>();
		e.getBindingResult().getFieldErrors().forEach(fieldError -> {
			String fieldName = fieldError.getField();
			String errorMessage = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
			errors.merge(fieldName, errorMessage, (oldVal, newVal) -> oldVal + ", " + newVal);
		});

		log.error("[handleMethodArgumentNotValid] {}", e.getMessage(), e);
		return handleExceptionInternalArgs(GlobalErrorStatus._VALIDATION_ERROR.getCode(), errors);
	}

	/**
	 * 6) Spring Security 인증/인가 예외 처리
	 */
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<BaseResponse<String>> handleAuthenticationException(AuthenticationException e) {
		log.error("[handleAuthenticationException] {}", e.getMessage(), e);
		return handleExceptionInternal(GlobalErrorStatus._UNAUTHORIZED.getCode());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<BaseResponse<String>> handleAccessDeniedException(AccessDeniedException e) {
		log.error("[handleAccessDeniedException] {}", e.getMessage(), e);
		return handleExceptionInternal(GlobalErrorStatus._ACCESS_DENIED.getCode());
	}

	/**
	 * ==============
	 * 내부 메서드
	 * ==============
	 */
	private ResponseEntity<BaseResponse<String>> handleExceptionInternal(BaseCode errorCode) {
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(BaseResponse.onFailure(errorCode.getCode(), errorCode.getMessage(), null));
	}

	private ResponseEntity<Object> handleExceptionInternalArgs(BaseCode errorCode, Map<String, String> errorArgs) {
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(BaseResponse.onFailure(errorCode.getCode(), errorCode.getMessage(), errorArgs));
	}

	private ResponseEntity<BaseResponse<String>> handleExceptionInternalFalse(BaseCode errorCode,
		String errorPoint) {
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(BaseResponse.onFailure(errorCode.getCode(), errorCode.getMessage(), errorPoint));
	}
}
