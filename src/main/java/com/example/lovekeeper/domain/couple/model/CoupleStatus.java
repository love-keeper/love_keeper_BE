package com.example.lovekeeper.domain.couple.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoupleStatus {

	CONNECTED("연결됨"),
	DISCONNECTED("연결되지 않음");

	private final String status;

	public static CoupleStatus of(String status) {
		for (CoupleStatus coupleStatus : values()) {
			if (coupleStatus.getStatus().equals(status)) {
				return coupleStatus;
			}
		}
		throw new IllegalArgumentException("ConnectionStatus에 해당하는 상태가 없습니다.");
	}
}
