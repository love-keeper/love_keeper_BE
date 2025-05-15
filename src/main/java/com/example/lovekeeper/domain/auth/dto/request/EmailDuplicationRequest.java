package com.example.lovekeeper.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "이메일 중복 확인 요청")
public class EmailDuplicationRequest {

	@Schema(description = "이메일", example = "qkrehdrb0813@gmail.com")
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	private String email;

}
