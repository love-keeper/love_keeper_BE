package com.example.lovekeeper.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "비밀번호 재설정 요청")
public class ResetPasswordRequest {

	@Schema(description = "이메일", example = "qkrehdrb0813@gmail.com")
	private String email;

}
