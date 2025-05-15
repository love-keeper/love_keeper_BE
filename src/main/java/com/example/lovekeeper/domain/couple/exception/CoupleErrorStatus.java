package com.example.lovekeeper.domain.couple.exception;

import org.springframework.http.HttpStatus;

import com.example.lovekeeper.global.exception.code.BaseCode;
import com.example.lovekeeper.global.exception.code.BaseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoupleErrorStatus implements BaseCodeInterface {
	// 커플 생성/연결 관련 (COUPLE001 ~ COUPLE010)
	COUPLE_NOT_FOUND(HttpStatus.NOT_FOUND, "COUPLE001", "커플을 찾을 수 없습니다."),
	ALREADY_COUPLE(HttpStatus.CONFLICT, "COUPLE002", "이미 커플인 회원입니다."),
	INVALID_COUPLE_STATUS(HttpStatus.BAD_REQUEST, "COUPLE003", "잘못된 커플 상태입니다."),
	CANNOT_INVITE_SELF(HttpStatus.BAD_REQUEST, "COUPLE004", "자기 자신을 초대할 수 없습니다."),
	CANNOT_CONNECT_WITH_SELF(HttpStatus.BAD_REQUEST, "COUPLE005", "자기 자신과 커플이 될 수 없습니다."),
	INVALID_INVITE_CODE(HttpStatus.BAD_REQUEST, "COUPLE006", "유효하지 않은 초대 코드입니다."),
	EXPIRED_INVITE_CODE(HttpStatus.BAD_REQUEST, "COUPLE007", "만료된 초대 코드입니다."),
	ALREADY_USED_INVITE_CODE(HttpStatus.BAD_REQUEST, "COUPLE008", "이미 사용된 초대 코드입니다."),
	COUPLE_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "COUPLE009", "이미 삭제된 커플입니다."),
	COUPLE_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "COUPLE010", "커플 생성에 실패했습니다."),
	MEMBER_NOT_IN_COUPLE(HttpStatus.BAD_REQUEST, "COUPLE011", "커플에 속한 회원이 아닙니다."),

	// 커플 상태 변경 관련 (COUPLE011 ~ COUPLE020)
	INVALID_COUPLE_UPDATE(HttpStatus.BAD_REQUEST, "COUPLE011", "커플 상태 업데이트에 실패했습니다."),
	COUPLE_DELETION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "COUPLE012", "이미 커플 상태인 회원은 커플 삭제가 불가능합니다."),
	COUPLE_INACTIVE(HttpStatus.FORBIDDEN, "COUPLE013", "비활성화된 커플입니다."),
	SAME_NICKNAME(HttpStatus.BAD_REQUEST, "COUPLE014", "커플은 서로 같은 닉네임으로 변경할 수 없습니다."),
	;

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
