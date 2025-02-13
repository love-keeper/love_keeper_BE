package com.example.lovekeeper.domain.promise.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "약속 생성 요청")
public class PromiseRequest {

	@Schema(description = "약속 내용", example = "매일 10시에 전화하기")
	@NotBlank(message = "약속 내용은 필수입니다.")
	private String content;

}
