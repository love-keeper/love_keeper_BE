package com.example.lovekeeper.domain.promise.exception;

import org.springframework.http.HttpStatus;

import com.example.lovekeeper.global.exception.code.BaseCode;
import com.example.lovekeeper.global.exception.code.BaseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PromiseErrorStatus implements BaseCodeInterface {

	// 약속을 찾을 수 없음
	PROMISE_NOT_FOUND(HttpStatus.NOT_FOUND, "PROMISE404", "약속을 찾을 수 없습니다."),

	// 중복된 약속
	PROMISE_DUPLICATE(HttpStatus.CONFLICT, "PROMISE409", "중복된 약속이 존재합니다."),

	// 약속 생성 실패
	PROMISE_CREATE_FAIL(HttpStatus.BAD_REQUEST, "PROMISE4001", "약속 생성에 실패했습니다."),

	// 약속 삭제 실패
	PROMISE_DELETE_FAIL(HttpStatus.BAD_REQUEST, "PROMISE4002", "약속 삭제에 실패했습니다."),

	// 접근 권한 없음
	PROMISE_FORBIDDEN(HttpStatus.FORBIDDEN, "PROMISE403", "해당 약속에 대한 접근 권한이 없습니다.");

	private final HttpStatus httpStatus;
	private final boolean isSuccess = false;
	private final String code;
	private final String message;

	@Override
	public BaseCode getCode() {
		return BaseCode.builder()
			.httpStatus(httpStatus)
			.isSuccess(isSuccess)
			.code(code)
			.message(message)
			.build();
	}
}
