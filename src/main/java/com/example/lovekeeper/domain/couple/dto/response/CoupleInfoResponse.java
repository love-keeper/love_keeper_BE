package com.example.lovekeeper.domain.couple.dto.response;

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
@Schema(description = "커플 정보 응답")
public class CoupleInfoResponse {

	@Schema(description = "커플 ID", example = "1")
	private Long coupleId;

	@Schema(description = "파트너 닉네임", example = "partner")
	private String partnerNickname;

	@Schema(description = "내 프로필 이미지 URL", example = "https://lovekeeper.com/profile.jpg")
	private String myProfileImageUrl;

	@Schema(description = "파트너 프로필 이미지 URL", example = "https://lovekeeper.com/profile.jpg")
	private String partnerProfileImageUrl;

	@Schema(description = "커플 시작일", example = "2021-01-01")
	private LocalDate startedAt;

	@Schema(description = "커플 기간", example = "100")
	private Long days;

	public static CoupleInfoResponse of(Long coupleId, String partnerNickname, String myProfileImageUrl,
		String partnerProfileImageUrl,
		LocalDate startedAt, Long days) {
		return CoupleInfoResponse.builder()
			.coupleId(coupleId)
			.partnerNickname(partnerNickname)
			.myProfileImageUrl(myProfileImageUrl)
			.partnerProfileImageUrl(partnerProfileImageUrl)
			.startedAt(startedAt)
			.days(days)
			.build();
	}
}
