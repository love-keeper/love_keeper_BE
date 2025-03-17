package com.example.lovekeeper.domain.draft.model;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum DraftType {

	CONCILIATION("화해"),
	ANSWER("답장"),
	;

	private final String title;

	// title로 Enum을 찾는 메서드 (선택적)
	public static DraftType fromTitle(String title) {
		return Arrays.stream(DraftType.values())
			.filter(draftType -> draftType.getTitle().equals(title))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("해당 title에 맞는 DraftType이 없습니다: " + title));
	}

}
