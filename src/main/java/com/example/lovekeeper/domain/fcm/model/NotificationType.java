package com.example.lovekeeper.domain.fcm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {

	LETTER("편지", "편지가 도착했어요!"),
	PROMISE("약속", "약속이 있어요!"),
	;

	private String title;
	private String description;
	
}
