package com.example.lovekeeper.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.example.lovekeeper.global.exception.code.BaseCode;
import com.example.lovekeeper.global.exception.code.BaseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthErrorStatus implements BaseCodeInterface {
	// 인증 관련 에러 (AUTH001 ~ AUTH010)
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH001", "유효하지 않은 토큰입니다."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH002", "만료된 토큰입니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH003", "유효하지 않은 리프레시 토큰입니다."),

	// 회원가입 관련 에러 (AUTH011 ~ AUTH020)
	DUPLICATE_EMAIL(HttpStatus.CONFLICT, "AUTH011", "이미 사용 중인 이메일입니다."),
	INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "AUTH012", "잘못된 이메일 형식입니다."),
	INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "AUTH013", "비밀번호는 8~20자의 영문, 숫자, 특수문자를 포함해야 합니다."),
	INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "AUTH014", "닉네임은 2~10자 이내여야 합니다."),
	PROFILE_IMAGE_UPLOAD_FAIL(HttpStatus.BAD_REQUEST, "AUTH015", "프로필 이미지 업로드에 실패했습니다."),
	CONSENT_REQUIRED(HttpStatus.BAD_REQUEST, "AUTH016", "필수 동의 항목을 체크해주세요."),
	INACTIVE_ACCOUNT(HttpStatus.FORBIDDEN, "AUTH017", "비활성화된 계정입니다."),

	// 로그인 관련 에러 (AUTH021 ~ AUTH030)
	LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH021", "이메일 또는 비밀번호가 일치하지 않습니다."),
	ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "AUTH022", "비활성화된 계정입니다."),
	ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "AUTH023", "잠긴 계정입니다."),
	ACCOUNT_DELETED(HttpStatus.FORBIDDEN, "AUTH024", "삭제된 계정입니다."),

	// 권한 관련 에러 (AUTH031 ~ AUTH040)
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH031", "접근 권한이 없습니다."),
	INSUFFICIENT_PERMISSIONS(HttpStatus.FORBIDDEN, "AUTH032", "필요한 권한이 없습니다."),

	// 소셜 로그인 관련 에러 (AUTH041 ~ AUTH050)
	SOCIAL_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH041", "소셜 로그인에 실패했습니다."),
	INVALID_SOCIAL_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH042", "유효하지 않은 소셜 토큰입니다."),

	// 이메일 관련 에러 (AUTH051 ~ AUTH060)
	EMAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH051", "이메일 발송에 실패했습니다."),
	EMAIL_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "AUTH052", "이메일 인증에 실패했습니다."),

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