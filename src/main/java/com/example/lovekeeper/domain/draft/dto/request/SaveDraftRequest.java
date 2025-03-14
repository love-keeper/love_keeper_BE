package com.example.lovekeeper.domain.draft.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "편지 임시 저장 요청 DTO")
public class SaveDraftRequest {

	@Schema(description = "저장할 순서 (1~4)", example = "1", required = true)
	@Min(value = 1, message = "순서는 1 이상이어야 합니다.")
	@Max(value = 4, message = "순서는 4 이하여야 합니다.")
	private int draftOrder;

	@Schema(description = "편지 내용", example = "미안해...", required = true)
	private String content;
}