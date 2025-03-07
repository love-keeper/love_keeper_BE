package com.example.lovekeeper.domain.fcm.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PushNotificationResponse {
	private Long id;
	private String title;
	private String body;
	private String relativeTime; // "1일 전" 같은 형식
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
