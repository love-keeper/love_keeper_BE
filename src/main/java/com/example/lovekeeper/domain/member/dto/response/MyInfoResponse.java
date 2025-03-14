package com.example.lovekeeper.domain.member.dto.response;

import java.time.LocalDate;

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
@Schema(description = "내 프로필 정보 응답 DTO")
public class MyInfoResponse {

	@Schema(description = "회원 ID", example = "1", required = true)
	private Long memberId;

	@Schema(description = "닉네임", example = "lover", required = true)
	private String nickname;

	@Schema(description = "생일 (yyyy-MM-dd)", example = "1999-01-01", required = true)
	private LocalDate birthday;

	@Schema(description = "연애 시작일 (yyyy-MM-dd)", example = "2023-01-01", nullable = true)
	private LocalDate relationshipStartDate;

	@Schema(description = "이메일", example = "qkrehdrb0813@gmail.com", required = true)
	private String email;

	@Schema(description = "프로필 이미지 URL", example = "https://s3.amazonaws.com/bucket/profile.jpg", nullable = true)
	private String profileImageUrl;

	@Schema(description = "커플 상대방 닉네임", example = "honey", nullable = true)
	private String coupleNickname;

	public static MyInfoResponse of(Long memberId, String nickname, LocalDate birthday, LocalDate relationshipStartDate,
		String email, String profileImageUrl, String coupleNickname) {
		return MyInfoResponse.builder()
			.memberId(memberId)
			.nickname(nickname)
			.birthday(birthday)
			.relationshipStartDate(relationshipStartDate)
			.email(email)
			.profileImageUrl(profileImageUrl)
			.coupleNickname(coupleNickname)
			.build();
	}
}