package com.example.lovekeeper.domain.letter.exception;

import org.springframework.http.HttpStatus;

import com.example.lovekeeper.global.exception.code.BaseCode;
import com.example.lovekeeper.global.exception.code.BaseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LetterErrorStatus implements BaseCodeInterface {
	LETTER_NOT_FOUND(HttpStatus.NOT_FOUND, "LETTER001", "편지를 찾을 수 없습니다."),
	NO_PERMISSION_TO_ACCESS_LETTER(HttpStatus.FORBIDDEN, "LETTER002", "편지에 접근할 권한이 없습니다.");

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
