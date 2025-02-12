package com.example.lovekeeper.domain.draft.dto.response;

import com.example.lovekeeper.domain.draft.model.Draft;

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
@Schema(description = "임시 저장된 편지 응답")
public class DraftResponse {

	@Schema(description = "임시 저장된 편지의 순서")
	private int order;

	@Schema(description = "임시 저장된 편지의 내용")
	private String content;

	public static DraftResponse of(Draft draft) {
		return DraftResponse.builder()
			.order(draft.getDraftOrder())
			.content(draft.getContent())
			.build();
	}
}
