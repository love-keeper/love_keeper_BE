package com.example.lovekeeper.domain.fcm.dto.response;

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
@Schema(description = "개별 푸시 알림 응답")
public class PushNotificationResponse {

	@Schema(description = "알림 ID", example = "1", required = true)
	private Long id;

	@Schema(description = "알림 제목", example = "새 편지 도착", required = true)
	private String title;

	@Schema(description = "알림 내용", example = "파트너로부터 새 편지가 도착했습니다.", required = true)
	private String body;

	@Schema(description = "상대적 시간 표현 (예: '1일 전')", example = "1일 전", required = true)
	private String relativeTime;

	@Schema(description = "읽음 여부", example = "false", required = true)
	private boolean isRead;

	public static PushNotificationResponse of(Long id, String title, String body, String relativeTime, boolean isRead) {
		return PushNotificationResponse.builder()
			.id(id)
			.title(title)
			.body(body)
			.relativeTime(relativeTime)
			.isRead(isRead)
			.build();
	}
}