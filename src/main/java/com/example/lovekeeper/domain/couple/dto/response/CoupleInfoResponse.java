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

	private Long coupleId;

	private String partnerNickname;

	private String myProfileImageUrl;

	private String partnerProfileImageUrl;

	private LocalDate startedAt;

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
