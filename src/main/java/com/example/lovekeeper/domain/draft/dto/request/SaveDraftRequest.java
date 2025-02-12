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
@Schema(description = "편지 임시 저장 요청")
public class SaveDraftRequest {

	@Schema(description = "순서", example = "1")
	@Min(1)
	@Max(4)
	private int draftOrder;

	@Schema(description = "편지 내용", example = "미안해...")
	private String content;

}
