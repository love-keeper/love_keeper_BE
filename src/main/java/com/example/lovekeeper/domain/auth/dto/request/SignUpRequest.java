package com.example.lovekeeper.domain.auth.dto.request;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import com.example.lovekeeper.domain.member.model.Provider;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class SignUpRequest {

	@Schema(description = "사용자 이메일", required = true, example = "user@example.com")
	private String email;

	@Schema(description = "비밀번호 (로컬 회원가입 시 필수, 8~20자 영문/숫자/특수문자 포함)",
		required = false,
		example = "Password123!")
	private String password;

	@Schema(description = "닉네임 (2~10자)", required = true, example = "lover")
	private String nickname;

	@Schema(description = "생년월일 (yyyy-MM-dd 형식)", required = true, example = "1990-01-01")
	private LocalDate birthDate;

	@Schema(description = "프로필 이미지 파일 (선택 사항)", required = false)
	private MultipartFile profileImage;

	@Schema(description = "인증 제공자 (LOCAL, GOOGLE, KAKAO 등)",
		required = true,
		example = "LOCAL",
		implementation = Provider.class)
	private Provider provider;

	@Schema(description = "소셜 로그인 제공자 ID (소셜 로그인 시 필수)",
		required = false,
		example = "123456789")
	private String providerId;

	@Schema(description = "개인정보 처리 방침 동의 여부", required = true, example = "true")
	private boolean privacyPolicyAgreed;

	@Schema(description = "마케팅 정보 수신 동의 여부", required = true, example = "false")
	private boolean marketingAgreed;

	@Schema(description = "서비스 이용 약관 동의 여부", required = true, example = "true")
	private boolean termsOfServiceAgreed;

	public static SignUpRequest of(String email, String password, String nickname, LocalDate birthDate,
		MultipartFile profileImage, Provider provider, String providerId, boolean privacyPolicyAgreed,
		boolean marketingAgreed, boolean termsOfServiceAgreed) {
		return SignUpRequest.builder()
			.email(email)
			.password(password)
			.nickname(nickname)
			.birthDate(birthDate)
			.profileImage(profileImage)
			.provider(provider)
			.providerId(providerId)
			.privacyPolicyAgreed(privacyPolicyAgreed)
			.marketingAgreed(marketingAgreed)
			.termsOfServiceAgreed(termsOfServiceAgreed)
			.build();
	}
}