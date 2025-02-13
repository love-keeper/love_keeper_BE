package com.example.lovekeeper.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.example.lovekeeper.global.exception.code.BaseCode;
import com.example.lovekeeper.global.exception.code.BaseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberErrorStatus implements BaseCodeInterface {
	// 회원 조회/검증 관련 (MEMBER001 ~ MEMBER010)
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER001", "존재하지 않는 회원입니다."),
	MEMBER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "MEMBER002", "이미 탈퇴한 회원입니다."),
	MEMBER_INACTIVE(HttpStatus.FORBIDDEN, "MEMBER003", "비활성화된 회원입니다."),
	INVALID_MEMBER_STATUS(HttpStatus.BAD_REQUEST, "MEMBER004", "잘못된 회원 상태입니다."),
	SELF_INVITE_CODE(HttpStatus.BAD_REQUEST, "MEMBER005", "자신의 초대 코드로 커플을 연결할 수 없습니다."),

	// 회원 정보 중복/검증 관련 (MEMBER011 ~ MEMBER020)
	DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER011", "이미 사용 중인 이메일입니다."),
	DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "MEMBER012", "이미 사용 중인 닉네임입니다."),
	INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER013", "잘못된 이메일 형식입니다."),
	INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER014", "비밀번호는 8~20자의 영문, 숫자, 특수문자를 포함해야 합니다."),
	INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER015", "닉네임은 2~10자 이내여야 합니다."),
	INVALID_BIRTH_DATE(HttpStatus.BAD_REQUEST, "MEMBER016", "잘못된 생년월일입니다."),
	INVALID_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER017", "이메일이 일치하지 않습니다."),

	// 프로필 관련 (MEMBER021 ~ MEMBER030)
	PROFILE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER021", "프로필 이미지를 찾을 수 없습니다."),
	PROFILE_IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER022", "프로필 이미지 업로드에 실패했습니다."),
	INVALID_PROFILE_IMAGE_SIZE(HttpStatus.BAD_REQUEST, "MEMBER023", "프로필 이미지 크기는 5MB를 초과할 수 없습니다."),
	INVALID_PROFILE_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER024", "지원하지 않는 이미지 형식입니다."),

	// 커플 관련 (MEMBER031 ~ MEMBER040)
	ALREADY_HAS_PARTNER(HttpStatus.CONFLICT, "MEMBER031", "이미 커플이 있는 회원입니다."),
	PARTNER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER032", "상대방을 찾을 수 없습니다."),
	INVALID_PARTNER_STATUS(HttpStatus.BAD_REQUEST, "MEMBER033", "유효하지 않은 상대방 상태입니다."),
	CANNOT_INVITE_SELF(HttpStatus.BAD_REQUEST, "MEMBER034", "자기 자신을 초대할 수 없습니다."),
	CANNOT_CONNECT_WITH_SELF(HttpStatus.BAD_REQUEST, "MEMBER035", "자기 자신과 커플이 될 수 없습니다."),
	INVITE_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER036", "초대 코드를 찾을 수 없습니다."),
	ALREADY_COUPLE(HttpStatus.CONFLICT, "MEMBER037", "이미 커플인 회원입니다."),
	INVALID_COUPLE_FOR_LETTER(HttpStatus.BAD_REQUEST, "MEMBER038", "편지를 보낼 수 없는 커플입니다."),
	MUST_HAVE_INVITE_CODE(HttpStatus.BAD_REQUEST, "MEMBER039", "초대 코드를 받으려면 초대 코드를 생성해야합니다."),

	// 회원 수정/탈퇴 관련 (MEMBER041 ~ MEMBER050)
	PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "MEMBER041", "비밀번호가 일치하지 않습니다."),
	INVALID_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER042", "현재 비밀번호가 일치하지 않습니다."),
	SAME_AS_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER043", "현재 비밀번호와 동일한 비밀번호로 변경할 수 없습니다."),
	WITHDRAWAL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "MEMBER044", "커플 상태에서는 회원 탈퇴가 불가능합니다."),

	// 초대 코드 관련 (MEMBER051 ~ MEMBER060)
	INVALID_INVITE_CODE(HttpStatus.BAD_REQUEST, "MEMBER051", "유효하지 않은 초대 코드입니다."),
	EXPIRED_INVITE_CODE(HttpStatus.BAD_REQUEST, "MEMBER052", "만료된 초대 코드입니다."),
	ALREADY_USED_INVITE_CODE(HttpStatus.BAD_REQUEST, "MEMBER053", "이미 사용된 초대 코드입니다.");

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