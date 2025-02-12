package com.example.lovekeeper.domain.draft.exception;

import org.springframework.http.HttpStatus;

import com.example.lovekeeper.global.exception.code.BaseCode;
import com.example.lovekeeper.global.exception.code.BaseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DraftErrorStatus implements BaseCodeInterface {
	// Draft 관련 오류 (DRAFT001 ~ DRAFT010)
	DRAFT_NOT_FOUND(HttpStatus.NOT_FOUND, "DRAFT001", "존재하지 않는 편지입니다."),
	DRAFT_ALREADY_EXISTS(HttpStatus.CONFLICT, "DRAFT002", "이미 존재하는 편지입니다."),
	DRAFT_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DRAFT003", "편지 저장에 실패했습니다."),
	DRAFT_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DRAFT004", "편지 업데이트에 실패했습니다."),
	DRAFT_DELETION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DRAFT005", "편지 삭제에 실패했습니다."),
	INVALID_ORDER(HttpStatus.BAD_REQUEST, "DRAFT006", "순서는 1~4 사이의 값이어야 합니다."),

	// 편지 관련 (DRAFT011 ~ DRAFT020)
	INVALID_DRAFT_ORDER(HttpStatus.BAD_REQUEST, "DRAFT011", "잘못된 편지 순서입니다."),
	DRAFT_CONTENT_EMPTY(HttpStatus.BAD_REQUEST, "DRAFT012", "편지 내용이 비어 있습니다."),

	// 기타 (DRAFT021 ~ DRAFT030)
	DRAFT_NOT_ALLOWED(HttpStatus.FORBIDDEN, "DRAFT021", "편지를 작성할 수 없는 상태입니다.");

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
