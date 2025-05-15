package com.example.lovekeeper.domain.member.dto.request;

import com.example.lovekeeper.domain.member.exception.annotation.UniqueEmail;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
@Schema(description = "이메일 변경 요청 DTO")
public class ChangeEmailRequest {

	@Schema(description = "변경할 새 이메일", example = "qkrehdrb0813@gmail.com", required = true)
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	@NotEmpty(message = "이메일을 입력해주세요.")
	@UniqueEmail
	private String email;

	@Schema(description = "인증 코드", example = "123456", required = true)
	@NotEmpty(message = "인증 코드를 입력해주세요.")
	private String code;
}