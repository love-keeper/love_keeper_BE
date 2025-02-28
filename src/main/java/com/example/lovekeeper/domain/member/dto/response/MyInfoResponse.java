package com.example.lovekeeper.domain.member.dto.response;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MyInfoResponse {

	private Long memberId;

	private String nickname;

	private LocalDate birthday;

	private LocalDate relationshipStartDate;

	private String email;

	private String profileImageUrl;

	public static MyInfoResponse of(Long memberId, String nickname, LocalDate birthday, LocalDate relationshipStartDate,
		String email, String profileImageUrl) {
		return MyInfoResponse.builder()
			.memberId(memberId)
			.nickname(nickname)
			.birthday(birthday)
			.relationshipStartDate(relationshipStartDate)
			.email(email)
			.profileImageUrl(profileImageUrl)
			.build();
	}
}
