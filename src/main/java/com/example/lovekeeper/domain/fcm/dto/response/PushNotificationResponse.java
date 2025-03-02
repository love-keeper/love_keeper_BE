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
}
