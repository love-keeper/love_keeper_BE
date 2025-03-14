package com.example.lovekeeper.domain.letter.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "편지 보내기 요청 DTO")
public class SendLetterRequest {

	@Schema(description = "편지 내용", required = true, example = "미안해!!")
	@NotBlank(message = "편지 내용은 필수 입력값입니다.")
	private String content;
}