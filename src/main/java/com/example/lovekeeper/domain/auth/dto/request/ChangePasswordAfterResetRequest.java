package com.example.lovekeeper.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "비밀번호 초기화 후 변경 요청")
public class ChangePasswordAfterResetRequest {

	@Schema(description = "이메일", example = "qkrehdrb0813@gmail.com")
	@NotBlank(message = "이메일을 입력해주세요.")
	private String email;

	@Schema(description = "비밀번호", example = "password1234")
	@NotBlank(message = "비밀번호를 입력해주세요.")
	private String password;

	@Schema(description = "비밀번호 확인", example = "password1234")
	@NotBlank(message = "비밀번호 확인을 입력해주세요.")
	private String passwordConfirm;

}
