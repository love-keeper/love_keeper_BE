package com.example.lovekeeper.domain.member.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatus {

	ACTIVE("활성화"),
	INACTIVE("비활성화"),
	DELETED("삭제");

	private final String description;

	public static MemberStatus of(String description) {
		for (MemberStatus memberStatus : values()) {
			if (memberStatus.getDescription().equals(description)) {
				return memberStatus;
			}
		}
		throw new IllegalArgumentException("MemberStatus에 해당하는 상태가 없습니다.");
	}

	public boolean isActive() {
		return this == ACTIVE;
	}

}
