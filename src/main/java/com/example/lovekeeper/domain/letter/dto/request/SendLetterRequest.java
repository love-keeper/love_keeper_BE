package com.example.lovekeeper.domain.letter.dto.request;

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
@Schema(description = "편지 보내기 요청")
public class SendLetterRequest {

	@Schema(description = "편지 내용", example = "미안해!!")
	private String content;

}
